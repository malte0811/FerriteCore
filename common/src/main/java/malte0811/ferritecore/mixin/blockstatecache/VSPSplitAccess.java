package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.util.math.shapes.PartSplitVoxelShape;
import net.minecraft.util.math.shapes.VoxelShapePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PartSplitVoxelShape.class)
public interface VSPSplitAccess {
    @Accessor
    VoxelShapePart getPart();

    @Accessor
    int getStartX();

    @Accessor
    int getStartY();

    @Accessor
    int getStartZ();

    @Accessor
    int getEndX();

    @Accessor
    int getEndY();

    @Accessor
    int getEndZ();
}
