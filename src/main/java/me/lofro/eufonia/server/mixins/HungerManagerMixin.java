package me.lofro.eufonia.server.mixins;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    // Hunger keeps at the same level the whole time.
    /*@ModifyArg( method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"), index = 0)
    public int removeHungerDecrease(int a) {
        return ((HungerManager)(Object) this).getFoodLevel();
    }*/

}
