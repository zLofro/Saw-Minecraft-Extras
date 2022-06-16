package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.game.interfaces.IPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {
    @Shadow private boolean leftOwner;

    @Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
    public void handleCanHitVanish(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.isSpectator() || !entity.isAlive() || !entity.collides()) {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }

        Entity owner = ((ProjectileEntity)(Object) this).getOwner();

        cir.setReturnValue(
            (
                !(owner instanceof ServerPlayerEntity) || !(entity instanceof ServerPlayerEntity) ||
                ((IPlayer) owner).canSeeOtherPlayer((ServerPlayerEntity) entity)
            ) &&
            (
                owner == null || leftOwner || !owner.isConnectedThroughVehicle(entity))
            );
        cir.cancel();
    }

    //TODO CAMBIAR MAPAS.

}
