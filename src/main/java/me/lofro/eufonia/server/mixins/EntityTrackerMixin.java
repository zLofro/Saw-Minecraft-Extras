package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.game.interfaces.IEntityTracker;
import me.lofro.eufonia.server.game.interfaces.IEntityTrackerImpl;
import me.lofro.eufonia.server.game.interfaces.IPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;

@Mixin(targets = "net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker")
public class EntityTrackerMixin implements IEntityTracker, IEntityTrackerImpl {

    @Shadow @Final private Set<EntityTrackingListener> listeners;

    @Shadow @Final Entity entity;

    @Override
    public void stopTracking0(ServerPlayerEntity player) {
        stopTracking(player);
    }

    @Override
    public void updateTrackingStatus0(ServerPlayerEntity player) {
        updateTrackedStatus(player);
    }

    @Override
    public Set<EntityTrackingListener> getListeners() {
        return listeners;
    }

    @ModifyVariable(method = "updateTrackedStatus(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "LOAD"))
    public boolean bl(boolean bl, ServerPlayerEntity player) {
        return ((IPlayer) player).canSeeOtherPlayer(entity) && bl;
    }
}
