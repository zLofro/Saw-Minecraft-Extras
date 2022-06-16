package me.lofro.eufonia.server.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.lofro.eufonia.server.game.interfaces.IEntityTracker;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface IThreadedAnvilChunkStorage {
    @Accessor
    Int2ObjectMap<IEntityTracker> getEntityTrackers();
}
