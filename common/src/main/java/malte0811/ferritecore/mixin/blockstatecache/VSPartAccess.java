package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.util.math.shapes.VoxelShapePart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VoxelShapePart.class)
public interface VSPartAccess {
    @Accessor
    int getXSize();

    @Accessor
    int getYSize();

    @Accessor
    int getZSize();
}
