package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;

@Mixin(BitSetVoxelShapePart.class)
public interface VSPBitSetAccess {
    @Accessor
    BitSet getBitSet();

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
