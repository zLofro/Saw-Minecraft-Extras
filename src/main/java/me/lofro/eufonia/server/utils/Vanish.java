package me.lofro.eufonia.server.utils;

import me.lofro.eufonia.server.game.interfaces.IPlayer;
import me.lofro.eufonia.server.game.interfaces.IThreadedAnvilChunkStorage;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class Vanish {

    public static void hide(ServerPlayerEntity hideThis, ServerPlayerEntity otherPlayer) {
        if (((IPlayer) otherPlayer).canSeeOtherPlayer(hideThis)) return;

        var tracker = otherPlayer.getWorld().getChunkManager().threadedAnvilChunkStorage;

        var entityTracker = ((IThreadedAnvilChunkStorage) tracker).getEntityTrackers().get(hideThis.getId());

        if (entityTracker != null) {
            entityTracker.stopTracking0(otherPlayer);
        }

        otherPlayer.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, hideThis));
    }

    public static void show(ServerPlayerEntity showThis, ServerPlayerEntity otherPlayer) {
        if (!((IPlayer) otherPlayer).canSeeOtherPlayer(showThis)) return;

        otherPlayer.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, showThis));

        var tracker = otherPlayer.getWorld().getChunkManager().threadedAnvilChunkStorage;

        var entityTracker = ((IThreadedAnvilChunkStorage) tracker).getEntityTrackers().get(showThis.getId());

        if (entityTracker!= null && !entityTracker.getListeners().contains(otherPlayer.networkHandler)) {
            entityTracker.updateTrackingStatus0(otherPlayer);
        }
    }

    public static void updatePlayer(ServerPlayerEntity player, GameMode prevGameMode) {
        boolean isCreative = prevGameMode == GameMode.CREATIVE; // Optimization bypass
        boolean enabled = IPlayer.enabled(player);
        boolean isBasedOnOp = enabled && IPlayer.basedOnOp(player);
        boolean isOp = isBasedOnOp && isCreative || player.hasPermissionLevel(4);
        player.getWorld().getPlayers().forEach(p -> {
            if (player == p) return;

            GameMode pGameMode = p.interactionManager.getGameMode();
            boolean pOp = isBasedOnOp && p.hasPermissionLevel(4);
            if (isCreative || IPlayer.canSeeOtherPlayer(enabled, prevGameMode, pGameMode, isBasedOnOp, isOp, pOp)) {
                Vanish.hide(p, player);
            } else {
                Vanish.show(p, player);
            }
            if (IPlayer.canSeeOtherPlayer(enabled, pGameMode, prevGameMode, isBasedOnOp, isOp, pOp)) {
                Vanish.hide(player, p);
            } else {
                Vanish.show(player, p);
            }
        });
    }

}
