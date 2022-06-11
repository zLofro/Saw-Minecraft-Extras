package me.lofro.eufonia;

import me.lofro.eufonia.server.ServerManager;
import me.lofro.eufonia.server.commands.SawExtrasCMD;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class SawExtras implements ModInitializer {

    private ServerManager serverManager;

    @Override
    public void onInitialize() {
        this.serverManager = new ServerManager(this);

        CommandRegistrationCallback.EVENT.register(SawExtrasCMD::register);
    }

}
