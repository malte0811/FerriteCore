package malte0811.ferritecore.mixin.blockstatecache;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArrayVoxelShape.class)
public interface VSArrayAccess {
    @Accessor("xs")
    void setXPoints(DoubleList newPoints);

    @Accessor("ys")
    void setYPoints(DoubleList newPoints);

    @Accessor("zs")
    void setZPoints(DoubleList newPoints);

    @Accessor("xs")
    DoubleList getXPoints();

    @Accessor("ys")
    DoubleList getYPoints();

    @Accessor("zs")
    DoubleList getZPoints();
}
