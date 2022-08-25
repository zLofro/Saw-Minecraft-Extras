package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.game.interfaces.IEntityTracker;
import me.lofro.eufonia.server.game.interfaces.IPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(targets = "net.minecraft.server.world.ThreadedAnvilChunkStorage$EntityTracker")
public abstract class EntityTrackerMixin implements IEntityTracker {

    @Shadow @Final private Set<EntityTrackingListener> listeners;
    @Shadow public abstract void stopTracking(ServerPlayerEntity player);
    @Shadow public abstract void updateTrackedStatus(ServerPlayerEntity player);

    @Override
    public void stopTrackingPlayer(ServerPlayerEntity player) {
        stopTracking(player);
    }

    @Override
    public void updateTrackingStatusOfPlayer(ServerPlayerEntity player) {
        updateTrackedStatus(player);
    }

    @Override
    public Set<EntityTrackingListener> getListeners() {
        return listeners;
    }

    @Redirect(method = "updateTrackedStatus(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;canBeSpectated(Lnet/minecraft/server/network/ServerPlayerEntity;)Z"))
    public boolean bl(Entity instance, ServerPlayerEntity spectator) {
        return instance.canBeSpectated(spectator) && (((IPlayer) spectator).canSeeOtherPlayer(instance));
    }
}
