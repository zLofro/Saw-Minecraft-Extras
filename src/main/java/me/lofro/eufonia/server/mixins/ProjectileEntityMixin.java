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
        if (!entity.isSpectator() && entity.isAlive() && entity.collides()) {
            Entity entity2 = ((ProjectileEntity)(Object)this).getOwner();

            if (entity2 instanceof ServerPlayerEntity && entity instanceof ServerPlayerEntity) {
                if (!((IPlayer)entity2).canSeeOtherPlayer(entity)) cir.setReturnValue(false);
            }

            cir.setReturnValue(entity2 == null || leftOwner || !entity2.isConnectedThroughVehicle(entity));
        } else {
            cir.setReturnValue(false);
        }
    }

    //TODO CAMBIAR MAPAS.

}
