package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;

public interface IEntityTrackerImpl {

    default void stopTracking(ServerPlayerEntity player) {
        throw new IllegalStateException();
    }

    default void updateTrackedStatus(ServerPlayerEntity player) {
        throw new IllegalStateException();
    }


}
