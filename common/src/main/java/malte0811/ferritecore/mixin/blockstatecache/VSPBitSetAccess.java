package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;

@Mixin(BitSetDiscreteVoxelShape.class)
public interface VSPBitSetAccess {
    @Accessor
    BitSet getStorage();

    @Accessor
    int getXMin();

    @Accessor
    int getYMin();

    @Accessor
    int getZMin();

    @Accessor
    int getXMax();

    @Accessor
    int getYMax();

    @Accessor
    int getZMax();
}
