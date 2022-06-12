package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.game.interfaces.IPlayer;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "setBlockBreakingInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getId()I"), cancellable = true)
    public void addContinue(int entityId, BlockPos pos, int progress, CallbackInfo ci) {
        var players = server.getPlayerManager().getPlayerList();

        var entityHuman = ((ServerWorld)(Object)this).getEntityById(entityId);

        players.forEach(p -> {
            // noinspection ConstantConditions
            if (p != null && p.world == (Object) this && p.getId() != entityId) {
                double d = (double)pos.getX() - p.getX();
                double e = (double)pos.getY() - p.getY();
                double f = (double)pos.getZ() - p.getZ();

                if (entityHuman != null && !((IPlayer)p).canSeeOtherPlayer(entityHuman)) {
                    return;
                }

                if (d * d + e * e + f * f < 1024.0D) {
                    p.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
                }
            }
        });

        ci.cancel();
    }

}
