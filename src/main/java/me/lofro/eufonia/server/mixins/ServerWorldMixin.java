package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.SawExtras;
import me.lofro.eufonia.server.game.interfaces.IPlayer;
import me.lofro.eufonia.server.game.interfaces.IWorld;
import me.lofro.eufonia.server.utils.Vanish;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements IWorld {

    private boolean vanishEnabled;
    @Override
    public boolean vanishEnabled() {
        return vanishEnabled;
    }
    private boolean advOnlySeesSrv;
    @Override
    public boolean advOnlySeesSrv() {
        return advOnlySeesSrv;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void ServerWorld_newInstance(MinecraftServer server,
                                        Executor workerExecutor,
                                        LevelStorage.Session session,
                                        ServerWorldProperties properties,
                                        RegistryKey<World> worldKey,
                                        RegistryEntry<DimensionType> registryEntry,
                                        WorldGenerationProgressListener worldGenerationProgressListener,
                                        ChunkGenerator chunkGenerator,
                                        boolean debugWorld,
                                        long seed,
                                        List<Spawner> spawners,
                                        boolean shouldTickTime,
                                        CallbackInfo ci) {
        String worldName = ((ServerWorld) (Object) this).getRegistryKey().getValue().toUnderscoreSeparatedString();
        vanishEnabled = IWorld.vanishEnabled(worldName);
        advOnlySeesSrv = IWorld.advOnlySeesSrv(worldName);
    }

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "setBlockBreakingInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getId()I"), cancellable = true)
    public void addContinue(int entityId, BlockPos pos, int progress, CallbackInfo ci) {
        var players = server.getPlayerManager().getPlayerList();

        var entityHuman = ((ServerWorld)(Object) this).getEntityById(entityId);

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
