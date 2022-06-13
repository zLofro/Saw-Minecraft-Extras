package me.lofro.eufonia.server.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;

public class ServerPlayerStateEvents {

    public interface OnPlayerChangeGameMode {
        Event<OnPlayerChangeGameMode> EVENT = EventFactory.createArrayBacked(OnPlayerChangeGameMode.class,
                (listeners) -> (player, gameMode) -> {
                    for (OnPlayerChangeGameMode listener : listeners) {
                        ActionResult actionResult = listener.change(player, gameMode);

                        if (actionResult != ActionResult.PASS) {
                            return actionResult;
                        }
                    }
                    return ActionResult.PASS;
                });
        ActionResult change(ServerPlayerEntity player, GameMode gameMode);
    }

}
