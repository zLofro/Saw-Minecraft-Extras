package me.lofro.eufonia.server.mixins;

import me.lofro.eufonia.SawExtras;
import me.lofro.eufonia.server.game.interfaces.IPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Shadow protected abstract boolean checkStatePlacement();

    @Inject(method = "canPlace", at = @At("HEAD"), cancellable = true)
    public void canPlaceVanishCheck(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (SawExtras.INSTANCE.config().PLACE_BLOCK_INSIDE_VANISHED_PLAYER()) {
            PlayerEntity playerEntity = context.getPlayer();
            ShapeContext shapeContext = playerEntity == null ? ShapeContext.absent() : ShapeContext.of(playerEntity);
            cir.setReturnValue(
                (!this.checkStatePlacement() || state.canPlaceAt(context.getWorld(), context.getBlockPos())) &&
                canPlace((ServerWorld) context.getWorld(), state, (IPlayer) playerEntity, shapeContext, context.getBlockPos())
            );
            cir.cancel();
        }
    }

    private boolean canPlace(ServerWorld world, BlockState data, IPlayer source, ShapeContext context, BlockPos pos) {
        VoxelShape voxelShape = data.getCollisionShape(world, pos, context);
        if (voxelShape.isEmpty()) return true;
        voxelShape = voxelShape.offset(pos.getX(), pos.getY(), pos.getZ());
        if (voxelShape.isEmpty()) return true;

        for (Entity entity : world.getOtherEntities(null, voxelShape.getBoundingBox())) {
            if (!source.canSeeOtherPlayer(entity) || entity.isRemoved() || !entity.intersectionChecked) continue;
            if (VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND))
                return false;
        }
        return true;
    }
}
