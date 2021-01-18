package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.SplitVoxelShape;
import net.minecraft.util.math.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SplitVoxelShape.class)
public interface VSSplitAccess {
    @Accessor
    @Mutable
    void setShape(VoxelShape newShape);

    @Accessor
    VoxelShape getShape();

    @Accessor
    Direction.Axis getAxis();
}
