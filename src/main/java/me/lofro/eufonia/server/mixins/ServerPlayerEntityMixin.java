package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.game.interfaces.IPlayer;
import me.lofro.eufonia.server.game.interfaces.IWorld;
import me.lofro.eufonia.server.utils.Vanish;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IPlayer {
    @Shadow @Final public MinecraftServer server;

    public boolean canSeeOtherPlayer(ServerPlayerEntity otherPlayer) {
        return IPlayer.canSeeOtherPlayer((ServerPlayerEntity)(Object) this, otherPlayer);
    }

    @Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setWorld(Lnet/minecraft/server/world/ServerWorld;)V"))
    public void moveToWorld_setWorld(ServerPlayerEntity instance, ServerWorld destination) {
        assert instance == (Object) this;
        ServerWorld prevWorld = instance.getWorld();

        instance.setWorld(destination);
        destination.onPlayerChangeDimension(instance);

        Vanish.updatePlayer(instance, (IWorld) prevWorld);
    }

    @Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerChangeDimension(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void moveToWorld_onPlayerChangeDimension(ServerWorld instance, ServerPlayerEntity player) {
    }

    @Redirect(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setWorld(Lnet/minecraft/server/world/ServerWorld;)V"))
    public void teleport_setWorld(ServerPlayerEntity instance, ServerWorld targetWorld) {
        assert instance == (Object) this;
        ServerWorld prevWorld = instance.getWorld();

        instance.setWorld(targetWorld);
        targetWorld.onPlayerTeleport(instance);

        Vanish.updatePlayer(instance, (IWorld) prevWorld);
    }

    @Redirect(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerTeleport(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void teleport_onPlayerTeleport(ServerWorld instance, ServerPlayerEntity player) {
    }
}
