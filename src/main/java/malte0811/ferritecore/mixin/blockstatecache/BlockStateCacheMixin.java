package malte0811.ferritecore.mixin.blockstatecache;

import malte0811.ferritecore.impl.BlockStateCacheImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.block.AbstractBlock$AbstractBlockState$Cache")
public class BlockStateCacheMixin {
    @Redirect(
            method = "<init>(Lnet/minecraft/block/BlockState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/shapes/VoxelShapes;getFaceShape(Lnet/minecraft/util/math/shapes/VoxelShape;Lnet/minecraft/util/Direction;)Lnet/minecraft/util/math/shapes/VoxelShape;"
            )
    )
    private VoxelShape redirectFaceShape(VoxelShape shape, Direction face) {
        return BlockStateCacheImpl.redirectFaceShape(shape, face);
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/block/BlockState;)V",
            at = @At(
                    value = "INVOKE",
                    args = "debug = true",
                    target = "Lnet/minecraft/block/Block;getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;"
            )
    )
    private VoxelShape redirectGetCollisionShape(
            Block block, BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context
    ) {
        return BlockStateCacheImpl.redirectGetCollisionShape(block, state, worldIn, pos, context);
    }
}
