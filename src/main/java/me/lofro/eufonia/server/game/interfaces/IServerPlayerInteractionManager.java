package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.world.GameMode;

import javax.annotation.Nullable;

public interface IServerPlayerInteractionManager {

    default void setGameMode(GameMode gamemode, @Nullable GameMode prevGamemode) {
        throw new IllegalStateException();
    }

}
