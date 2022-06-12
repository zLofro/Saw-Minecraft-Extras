package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.events.ServerPlayerConnectionEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import java.util.function.Function;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    // Only sends the "multiplayer.player.left" message to OPS.
    @Redirect(method = "onDisconnected",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void changeLeaveDisplay(PlayerManager instance, Text message, MessageType type, UUID sender) {
        instance.getPlayerList().forEach(p -> {
            if (p.hasPermissionLevel(4)) p.sendMessage(Text.of(Formatting.LIGHT_PURPLE + message.getString()), false);
        });
    }

    // Hides chat messages sent by players without OP and sends messages sent by OPS to other OPS.
    @Redirect(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void filterChat(PlayerManager instance, Text serverMessage, Function<ServerPlayerEntity, Text> playerMessageFactory, MessageType type, UUID sender) {
        instance.getPlayerList().forEach(p -> {
            if (p.hasPermissionLevel(4)) {
                var senderPlayer = instance.getPlayer(sender);
                if (senderPlayer == null) return;

                if (senderPlayer.hasPermissionLevel(4)) p.sendMessage(serverMessage, false);
            }
        });
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void triggerPlayerJoinEvent(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ServerPlayerConnectionEvents.OnServerPlayerConnect.EVENT.invoker().connect(player, server);
    }

}
