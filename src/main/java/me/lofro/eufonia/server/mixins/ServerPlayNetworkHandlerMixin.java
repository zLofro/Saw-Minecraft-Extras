package me.lofro.eufonia.server.mixins;

import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Function;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Redirect(method = "onDisconnected",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void changeLeaveDisplay(PlayerManager instance, Text message, MessageType type, UUID sender) {
        instance.getPlayerList().forEach(p -> {
            if (p.hasPermissionLevel(4)) p.sendMessage(Text.of(Formatting.LIGHT_PURPLE + message.getString()), false);
        });
    }

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

}
