package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.NotImplementedException;

public interface IEntityTrackerImpl {
    default void stopTracking(ServerPlayerEntity player) {
        method_18733(player);
    }

    default void method_18733(ServerPlayerEntity player) {
        throw new NotImplementedException("Could not generate mixin for the class " + this.getClass());
    }

    default void updateTrackedStatus(ServerPlayerEntity player) {
        method_18736(player);
    }

    default void method_18736(ServerPlayerEntity player) {
        throw new NotImplementedException("Could not generate mixin for the class " + this.getClass());
    }
}
