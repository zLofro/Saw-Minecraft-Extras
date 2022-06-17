package me.lofro.eufonia.server.game.interfaces;

import me.lofro.eufonia.SawExtras;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public interface IPlayer {
    static boolean canSeeOtherPlayer(ServerPlayerEntity thisPlayer, ServerPlayerEntity otherPlayer) {
        if (otherPlayer == null) return true;

        IWorld thisPlayerWorld = (IWorld) thisPlayer.getWorld();
        boolean vanishEnabled = thisPlayerWorld.vanishEnabled() && thisPlayer != otherPlayer;

        return canSeeOtherPlayer(
                vanishEnabled,
                thisPlayer.interactionManager.getGameMode(),
                otherPlayer.interactionManager.getGameMode(),
                vanishEnabled && thisPlayerWorld.advOnlySeesSrv()
        );
    }


    /**
     * <p>We define see* as the possibility for a player to know another player exists.
     * This involves physically seeing the player, but also hearing sounds coming from them,
     * rendering their particles, knowing if they are online, etc.
     * <p>Players in {@code CREATIVE} can see* all players
     * <p>Players in {@code SURVIVAL} can only see* players in {@code SURVIVAL} and {@code ADVENTURE}
     * <p>Players in {@code SPECTATOR} can only see* players in {@code SURVIVAL} and {@code ADVENTURE}
     * <p>Players in {@code ADVENTURE} can only see* players in {@code SURVIVAL}. They also may see*
     * other players in {@code ADVENTURE} if {@code advOnlySeesSrv} is set to {@code false}.
     * @return Whether the first person {@code thisPlayer} (whose game mode is {@code thisPlayer})
     * is able to see the second person {@code otherPlayer} (whose game mode is {@code otherG})
     */
    static boolean canSeeOtherPlayer(boolean vanishEnabled, GameMode thisG, GameMode otherG, boolean advOnlySeesSrv) {
        return SawExtras.INSTANCE.config().DISABLE_VANISH() || !vanishEnabled || thisG == GameMode.CREATIVE || switch (otherG) {
            case SURVIVAL -> true;
            case ADVENTURE -> thisG != GameMode.ADVENTURE || !advOnlySeesSrv;
            default -> false;
        };
    }

    boolean canSeeOtherPlayer(ServerPlayerEntity otherPlayer);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canSeeOtherPlayer(Entity entity) {
        return !(entity instanceof ServerPlayerEntity) || canSeeOtherPlayer((ServerPlayerEntity) entity);
    }
}
