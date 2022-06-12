package me.lofro.eufonia;

import me.lofro.eufonia.server.ServerManager;
import me.lofro.eufonia.server.commands.SawExtrasCMD;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SawExtras implements ModInitializer {
    public static SawExtras INSTANCE;
    public static final Logger LOGGER = LoggerFactory.getLogger("sawextras");

    private ServerManager serverManager;

    @Override
    public void onInitialize() {
        INSTANCE = this;

        this.serverManager = new ServerManager(this);

        CommandRegistrationCallback.EVENT.register(SawExtrasCMD::register);
    }

    /**
     * @return the mod's server manager.
     */
    public ServerManager serverManager() {
        return serverManager;
    }

}
