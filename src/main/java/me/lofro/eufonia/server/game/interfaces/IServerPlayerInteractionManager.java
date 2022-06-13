package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.world.GameMode;
import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nullable;

public interface IServerPlayerInteractionManager {
    default void setGameMode(GameMode gameMode, @Nullable GameMode prevGameMode) {
        method_14261_impl(gameMode, prevGameMode);
    }

    void method_14261_impl(GameMode gameMode, @Nullable GameMode prevGameMode);

    default void method_14261(GameMode gameMode, @Nullable GameMode prevGameMode) {
        throw new NotImplementedException("Could not generate mixin for the class " + this.getClass());
    }
}
