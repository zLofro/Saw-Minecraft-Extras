package me.lofro.eufonia.server.utils;

import me.lofro.eufonia.server.game.interfaces.IPlayer;
import me.lofro.eufonia.server.mixins.IThreadedAnvilChunkStorage;
import me.lofro.eufonia.server.game.interfaces.IWorld;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.function.Consumer;

public class Vanish {

    public static void hide(ServerPlayerEntity hideThis, ServerPlayerEntity otherPlayer) {
        if (((IPlayer) otherPlayer).canSeeOtherPlayer(hideThis)) return;

        hide0(hideThis, otherPlayer);
    }
    private static void hide0(ServerPlayerEntity hideThis, ServerPlayerEntity otherPlayer) {
        var tracker = otherPlayer.getWorld().getChunkManager().threadedAnvilChunkStorage;

        var entityTracker = ((IThreadedAnvilChunkStorage) tracker).getEntityTrackers().get(hideThis.getId());

        if (entityTracker != null) {
            entityTracker.stopTrackingPlayer(otherPlayer);
        }

        otherPlayer.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, hideThis));
    }

    public static void show(ServerPlayerEntity showThis, ServerPlayerEntity otherPlayer) {
        if (!((IPlayer) otherPlayer).canSeeOtherPlayer(showThis)) return;

        show0(showThis, otherPlayer);
    }
    private static void show0(ServerPlayerEntity showThis, ServerPlayerEntity otherPlayer) {
        otherPlayer.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, showThis));

        var tracker = otherPlayer.getWorld().getChunkManager().threadedAnvilChunkStorage;

        var entityTracker = ((IThreadedAnvilChunkStorage) tracker).getEntityTrackers().get(showThis.getId());

        if (entityTracker!= null && !entityTracker.getListeners().contains(otherPlayer.networkHandler)) {
            entityTracker.updateTrackingStatusOfPlayer(otherPlayer);
        }
    }

    private static void forceReshow(ServerPlayerEntity showThis, ServerPlayerEntity otherPlayer) {
        hide0(showThis, otherPlayer);
        show0(showThis, otherPlayer);
    }

    public static void updatePlayer(ServerPlayerEntity player, IWorld prevWorld) {
        player.server.getPlayerManager().getPlayerList().forEach(updatePlayerDimensionChanged(player, prevWorld));
    }
    public static void updatePlayer(ServerPlayerEntity player, GameMode prevGameMode) {
        player.server.getPlayerManager().getPlayerList().forEach(updatePlayerGameModeChanged(player, prevGameMode));
    }

    private static Consumer<ServerPlayerEntity> updatePlayerGameModeChanged(ServerPlayerEntity player, GameMode prevGameMode) {
        IWorld playerWorld = (IWorld) player.getWorld();
        boolean playerEnabled = playerWorld.vanishEnabled();
        boolean playerEnabledAdvOnlySeesSrv = playerEnabled && playerWorld.advOnlySeesSrv();
        return p -> {
            if (player == p) return;
            GameMode pGameMode = p.interactionManager.getGameMode();

            if (IPlayer.canSeeOtherPlayer(playerEnabled, prevGameMode, pGameMode, playerEnabledAdvOnlySeesSrv)) {
                hide(p, player);
            } else {
                show(p, player);
            }

            IWorld pWorld = (IWorld) p.getWorld();
            if (IPlayer.canSeeOtherPlayer(pWorld.vanishEnabled(), pGameMode, prevGameMode, pWorld.advOnlySeesSrv())) {
                hide(player, p);
            } else {
                show(player, p);
            }
        };
    }

    private static Consumer<ServerPlayerEntity> updatePlayerDimensionChanged(ServerPlayerEntity player, IWorld prevWorld) {
        GameMode playerGameMode = player.interactionManager.getGameMode();
        boolean prevPlayerEnabled = prevWorld.vanishEnabled();
        boolean prevPlayerEnabledAdvOnlySeesSrv = prevPlayerEnabled && prevWorld.advOnlySeesSrv();
        return p -> {
            if (player == p) return;
            GameMode pGameMode = p.interactionManager.getGameMode();

            if (IPlayer.canSeeOtherPlayer(prevPlayerEnabled, playerGameMode, pGameMode, prevPlayerEnabledAdvOnlySeesSrv)) {
                hide(p, player);
            } else {
                forceReshow(p, player);
            }

            IWorld pWorld = (IWorld) p.getWorld();
            if (IPlayer.canSeeOtherPlayer(pWorld.vanishEnabled(), pGameMode, playerGameMode, pWorld.advOnlySeesSrv())) {
                hide(player, p);
            } else {
                show(player, p);
            }
        };
    }

}
