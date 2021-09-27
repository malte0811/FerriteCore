package malte0811.ferritecore.mixin.blockstatecache;

import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SubShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SubShape.class)
public interface VSPSplitAccess {
    @Accessor
    DiscreteVoxelShape getParent();

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
