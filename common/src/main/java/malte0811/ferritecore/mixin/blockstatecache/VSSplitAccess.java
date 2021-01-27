package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SliceShape.class)
public interface VSSplitAccess {
    @Accessor
    @Mutable
    void setDelegate(VoxelShape newShape);

    @Accessor
    VoxelShape getDelegate();

    @Accessor
    Direction.Axis getAxis();
}
