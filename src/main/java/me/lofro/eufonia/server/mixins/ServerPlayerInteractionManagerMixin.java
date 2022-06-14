package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.events.ServerPlayerStateEvents;
import me.lofro.eufonia.server.game.interfaces.IServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin implements IServerPlayerInteractionManager {
    @Shadow @Final protected ServerPlayerEntity player;

    @Shadow private GameMode gameMode;

    @Inject(method = "changeGameMode", at = @At("HEAD"), cancellable = true)
    public void triggerChangeGameModeEvent(GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        GameMode prevGameMode = this.gameMode;

        if (gameMode == prevGameMode) {
            cir.setReturnValue(false);
        } else {
            setGameMode(gameMode, prevGameMode);
            cir.setReturnValue(true);
        }

        ServerPlayerStateEvents.OnPlayerChangeGameMode.EVENT.invoker().change(player, prevGameMode);
        cir.cancel();
    }


    @Override
    public void method_14261_impl(GameMode gameMode, @Nullable GameMode prevGameMode) {
        method_14261(gameMode, prevGameMode);
    }
}
