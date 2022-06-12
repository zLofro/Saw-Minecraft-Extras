package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;

import java.util.Set;

public interface IEntityTracker {

    void stopTracking0(ServerPlayerEntity player);

    void updateTrackingStatus0(ServerPlayerEntity player);

    Set<EntityTrackingListener> getListeners();

}
