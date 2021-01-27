package malte0811.ferritecore.mixin.blockstatecache;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArrayVoxelShape.class)
public interface VSArrayAccess {
    @Accessor
    void setXs(DoubleList newPoints);

    @Accessor
    void setYs(DoubleList newPoints);

    @Accessor
    void setZs(DoubleList newPoints);

    @Accessor
    DoubleList getXs();

    @Accessor
    DoubleList getYs();

    @Accessor
    DoubleList getZs();
}
