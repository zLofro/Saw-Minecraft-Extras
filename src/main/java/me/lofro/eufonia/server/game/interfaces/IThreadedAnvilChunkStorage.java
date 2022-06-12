package me.lofro.eufonia.server.game.interfaces;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public interface IThreadedAnvilChunkStorage {

    Int2ObjectMap<IEntityTracker> getEntityTrackers();

}
