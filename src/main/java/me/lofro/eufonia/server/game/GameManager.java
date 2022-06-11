package me.lofro.eufonia.server.game;

import me.lofro.eufonia.server.ServerManager;
import net.minecraft.server.MinecraftServer;

public class GameManager {

    private final ServerManager serverManager;

    private final MinecraftServer server;

    public GameManager(final ServerManager serverManager, MinecraftServer server) {
        this.serverManager = serverManager;
        this.server = server;
    }

}
