package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.game.interfaces.IPlayer;
import me.lofro.eufonia.server.utils.Vanish;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IPlayer {

    public boolean canSeeOtherPlayer(ServerPlayerEntity otherPlayer) {
        return IPlayer.canSeeOtherPlayer(((ServerPlayerEntity)(Object)this), otherPlayer);
    }

    @Inject(method = "moveToWorld", at = @At("HEAD"))
    public void updateVanishAtWorldChange(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        var player = ((ServerPlayerEntity)(Object)this);

        if (player != null) Vanish.updatePlayer(player, player.interactionManager.getGameMode());
    }

}
