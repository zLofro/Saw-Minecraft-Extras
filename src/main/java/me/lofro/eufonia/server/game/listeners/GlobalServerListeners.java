package me.lofro.eufonia.server.game.listeners;

import me.lofro.eufonia.server.events.ServerPlayerConnectionEvents;
import me.lofro.eufonia.server.events.ServerPlayerStateEvents;
import me.lofro.eufonia.server.utils.Vanish;
import net.minecraft.util.ActionResult;

public class GlobalServerListeners {

    public void load(boolean alreadyEnabled) {
        if (!alreadyEnabled) {
            registerCallbacks();
        }
    }

    private void registerCallbacks() {
        onPlayerJoined();
        onPlayerChangeGameMode();
    }

    private void onPlayerJoined() {
        ServerPlayerConnectionEvents.OnServerPlayerConnect.EVENT.register((player, server) -> {
            player.server.getPlayerManager().getPlayerList().forEach(p -> {
                if (player == p) return;
                Vanish.hide(player, p);
                Vanish.hide(p, player);
            });

            return ActionResult.PASS;
        });
    }

    private void onPlayerChangeGameMode() {
        ServerPlayerStateEvents.OnPlayerChangeGameMode.EVENT.register((player, prevGameMode) -> {
            Vanish.updatePlayer(player, prevGameMode);

            return ActionResult.PASS;
        });
    }

}
