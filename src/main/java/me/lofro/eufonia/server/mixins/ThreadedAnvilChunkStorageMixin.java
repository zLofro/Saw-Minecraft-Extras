package me.lofro.eufonia.server.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.lofro.eufonia.server.game.interfaces.IEntityTracker;
import me.lofro.eufonia.server.game.interfaces.IThreadedAnvilChunkStorage;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements IThreadedAnvilChunkStorage {
    @Shadow @Final private Int2ObjectMap<?> entityTrackers;

    @Override
    @SuppressWarnings("unchecked")
    public Int2ObjectMap<IEntityTracker> getEntityTrackers() {
        return (Int2ObjectMap<IEntityTracker>) entityTrackers;
    }

}
