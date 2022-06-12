package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.events.ServerPlayerStateEvents;
import me.lofro.eufonia.server.game.interfaces.IServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin implements IServerPlayerInteractionManager {

    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(method = "changeGameMode", at = @At("HEAD"), cancellable = true)
    public void triggerChangeGamemodeEvent(GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        GameMode prevGameMode = ((ServerPlayerInteractionManager) (Object) this).getGameMode();

        if (gameMode == prevGameMode) {
            cir.setReturnValue(false);
        } else {
            setGameMode(gameMode, prevGameMode);
            cir.setReturnValue(true);
        }

        ServerPlayerStateEvents.OnPlayerChangeGamemode.EVENT.invoker().change(player, prevGameMode);
        cir.cancel();
    }

}
