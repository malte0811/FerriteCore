package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VSSplitAccess;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

public class VoxelShapeSplitHash implements Hash.Strategy<SliceShape> {
    public static final VoxelShapeSplitHash INSTANCE = new VoxelShapeSplitHash();

    @Override
    public int hashCode(SliceShape o) {
        VSSplitAccess access = access(o);
        int result = Objects.hashCode(access.getAxis());
        result = 31 * result + VoxelShapePartHash.INSTANCE.hashCode(getPart(o));
        result = 31 * result + VoxelShapeHash.INSTANCE.hashCode(access.getDelegate());
        return result;
    }

    @Override
    public boolean equals(SliceShape a, SliceShape b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        }
        VSSplitAccess accessA = access(a);
        VSSplitAccess accessB = access(b);
        return Objects.equals(accessA.getAxis(), accessB.getAxis()) &&
                VoxelShapeHash.INSTANCE.equals(accessA.getDelegate(), accessB.getDelegate()) &&
                VoxelShapePartHash.INSTANCE.equals(getPart(a), getPart(b));
    }

    private static VSSplitAccess access(SliceShape a) {
        return (VSSplitAccess) a;
    }

    private static DiscreteVoxelShape getPart(VoxelShape a) {
        return ((VoxelShapeAccess) a).getShape();
    }
}
