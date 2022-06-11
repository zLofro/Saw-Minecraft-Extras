package me.lofro.eufonia.server;

import me.lofro.eufonia.SawExtras;
import me.lofro.eufonia.server.game.GameManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerManager {

    private final SawExtras modManager;

    private GameManager gameManager;

    public ServerManager(final SawExtras modManager) {
        this.modManager = modManager;

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.gameManager = new GameManager(this, server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {

        });
    }

}
