package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public interface IPlayer {
    static boolean canSeeOtherPlayer(ServerPlayerEntity thisPlayer, ServerPlayerEntity otherPlayer) {
        if (otherPlayer == null) return true;

        boolean enabled = enabled(thisPlayer);

        return canSeeOtherPlayer(
            enabled,
            thisPlayer.interactionManager.getGameMode(),
            otherPlayer.interactionManager.getGameMode(),
            enabled && basedOnOp(thisPlayer) && thisPlayer != otherPlayer,
            thisPlayer.hasPermissionLevel(4),
            otherPlayer.hasPermissionLevel(4)
        );
    }

    static boolean canSeeOtherPlayer(boolean enabled, GameMode thisG, GameMode otherG, boolean basedOnOp, boolean thisOp, boolean otherOp) {
        return !enabled || thisG == GameMode.CREATIVE || switch (otherG) {
            case SURVIVAL, ADVENTURE -> !basedOnOp || thisOp || otherOp;
            default -> false;
        };
    }

    static boolean enabled(ServerPlayerEntity player) {
        return switch (player.getWorld().getRegistryKey().getValue().toUnderscoreSeparatedString()) {
            case "saw_forest", "saw_underground", "psicodelia_void_train" -> true;
            default -> false;
        };
    }

    static boolean basedOnOp(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().getValue().toUnderscoreSeparatedString().equals("saw_underground");
    }

    boolean canSeeOtherPlayer(ServerPlayerEntity otherPlayer);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canSeeOtherPlayer(Entity entity) {
        return !(entity instanceof ServerPlayerEntity) || canSeeOtherPlayer((ServerPlayerEntity) entity);
    }
}
