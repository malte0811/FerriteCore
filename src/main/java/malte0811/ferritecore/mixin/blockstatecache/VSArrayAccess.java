package malte0811.ferritecore.mixin.blockstatecache;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.shapes.VoxelShapeArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VoxelShapeArray.class)
public interface VSArrayAccess {
    @Accessor
    void setXPoints(DoubleList newPoints);

    @Accessor
    void setYPoints(DoubleList newPoints);

    @Accessor
    void setZPoints(DoubleList newPoints);

    @Accessor
    DoubleList getXPoints();

    @Accessor
    DoubleList getYPoints();

    @Accessor
    DoubleList getZPoints();
}
