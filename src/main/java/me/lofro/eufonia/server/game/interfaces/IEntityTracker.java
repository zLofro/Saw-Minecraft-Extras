package me.lofro.eufonia.server.game.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;

import java.util.Set;

public interface IEntityTracker {
    void stopTrackingPlayer(ServerPlayerEntity player);

    void updateTrackingStatusOfPlayer(ServerPlayerEntity player);

    Set<EntityTrackingListener> getListeners();

}
