package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.server.game.interfaces.IPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IPlayer {

    public boolean canSeeOtherPlayer(ServerPlayerEntity otherPlayer) {
        return IPlayer.canSeeOtherPlayer(((ServerPlayerEntity)(Object)this), otherPlayer);
    }

}
