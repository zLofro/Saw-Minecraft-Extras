package me.lofro.eufonia;

import me.lofro.eufonia.server.ServerManager;
import me.lofro.eufonia.server.commands.SawExtrasCMD;
import me.lofro.eufonia.util.DefaultConfig;
import me.lofro.eufonia.util.SimpleConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SawExtras implements ModInitializer {
    public static SawExtras INSTANCE;
    public static final Logger LOGGER = LoggerFactory.getLogger("sawextras");

    public Config config() {
        return config;
    }
    private Config config;
    public record Config(boolean DISABLE_VANISH, boolean PLACE_BLOCK_INSIDE_VANISHED_PLAYER) {
    }

    private ServerManager serverManager;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        SimpleConfig config = SimpleConfig.of("sawextras_config").provider(new DefaultConfig()
            .addVal("EnableVanish", true)
            .addVal("PlaceBlockInsideVanishedPlayer", true)
        ).request();
        this.config = new Config(
            !config.getOrDefault("EnableVanish", true),
            config.getOrDefault("PlaceBlockInsideVanishedPlayer", true)
        );

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
