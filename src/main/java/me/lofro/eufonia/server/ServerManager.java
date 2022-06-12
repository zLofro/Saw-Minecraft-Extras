package me.lofro.eufonia.server;

import me.lofro.eufonia.SawExtras;
import me.lofro.eufonia.server.game.listeners.GlobalServerListeners;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerManager {

    private final SawExtras modManager;

    private boolean isLoaded = false;

    public ServerManager(final SawExtras modManager) {
        this.modManager = modManager;

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {

            new GlobalServerListeners().load(isLoaded);
            isLoaded = true;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {

        });
    }

    /**
     * @return the mod's manager.
     */
    public SawExtras modManager() {
        return modManager;
    }

}
