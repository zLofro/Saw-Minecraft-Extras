package me.lofro.eufonia.server.mixins;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import me.lofro.eufonia.server.game.interfaces.IPlayer;
import me.lofro.eufonia.server.utils.Vanish;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow @Final private List<ServerPlayerEntity> players;

    @Shadow private int latencyUpdateTimer;

    // Only sends the "multiplayer.player.join" message to OPS.
    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void changeJoinDisplay(PlayerManager instance, Text message, MessageType type, UUID sender) {
        instance.getPlayerList().forEach(p -> {
            if (p.hasPermissionLevel(4)) p.sendMessage(message, false);
        });
    }

    @Inject(method = "sendToAround", at = @At("HEAD"), cancellable = true)
    public void addVanishImplementation(PlayerEntity player, double x, double y, double z, double distance, RegistryKey<World> worldKey, Packet<?> packet, CallbackInfo ci) {
        players.forEach(p -> {

            if (player != null && !((IPlayer)p).canSeeOtherPlayer(player)) {
                return;
            }

            if (p != player && p.world.getRegistryKey() == worldKey) {
                double d = x - p.getX();
                double e = y - p.getY();
                double f = z - p.getZ();
                if (d * d + e * e + f * f < distance * distance) {
                    p.networkHandler.sendPacket(packet);
                }
            }
        });

        ci.cancel();
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 7))
    public void handlePlayerListS2CPacket(ServerPlayNetworkHandler instance, Packet<?> packet) {
        var player = instance.player;

        if (!((IPlayer)player).canSeeOtherPlayer(player.server.getPlayerManager().getPlayer(((PlayerListS2CPacket)packet).getEntries().get(0).getProfile().getId()))) {
            return;
        }

        instance.sendPacket(packet);
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"))
    public void handlePlayerManagerOnConnectS2CPacketToAll(PlayerManager instance, Packet<?> packet) {
        instance.getPlayerList().forEach(p -> {
            if (((IPlayer)p).canSeeOtherPlayer(p.server.getPlayerManager().getPlayer(((PlayerListS2CPacket)packet).getEntries().get(0).getProfile().getId()))) {
                p.networkHandler.sendPacket(packet);
            }
        });
    }

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V"))
    public void handlePlayerManagerRemoveS2CPacketToAll(PlayerManager instance, Packet<?> packet) {
        instance.getPlayerList().forEach(p -> {
            if (((IPlayer)p).canSeeOtherPlayer(p.server.getPlayerManager().getPlayer(((PlayerListS2CPacket)packet).getEntries().get(0).getProfile().getId()))) {
                p.networkHandler.sendPacket(packet);
            } else {
                Vanish.show(p, p.server.getPlayerManager().getPlayer(((PlayerListS2CPacket)packet).getEntries().get(0).getProfile().getId()));
            }
        });
    }

    @Inject(method = "updatePlayerLatency", at = @At("HEAD"), cancellable = true)
    public void handleLatencyVanish(CallbackInfo ci) {

        if (++latencyUpdateTimer > 600) {

            players.forEach(target -> target.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, this.players.stream().filter((Predicate<ServerPlayerEntity>) input -> ((IPlayer)target).canSeeOtherPlayer(input)).collect(Collectors.toList()))));

            this.latencyUpdateTimer = 0;
        }

        ci.cancel();
    }

    @Inject(method = "addToOperators", at = @At("TAIL"))
    public void addOperatorVanish(GameProfile profile, CallbackInfo ci) {
        var player = ((PlayerManager)(Object)this).getPlayer(profile.getId());

        if (player != null) Vanish.updatePlayer(player, player.interactionManager.getGameMode());
    }

    @Inject(method = "removeFromOperators", at = @At("TAIL"))
    public void removeOperatorVanish(GameProfile profile, CallbackInfo ci) {
        var player = ((PlayerManager)(Object)this).getPlayer(profile.getId());

        if (player != null) Vanish.updatePlayer(player, player.interactionManager.getGameMode());
    }

}
