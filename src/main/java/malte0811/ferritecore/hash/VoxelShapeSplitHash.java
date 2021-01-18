package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VSSplitAccess;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import net.minecraft.util.math.shapes.SplitVoxelShape;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapePart;

import java.util.Objects;

public class VoxelShapeSplitHash implements Hash.Strategy<SplitVoxelShape> {
    public static final VoxelShapeSplitHash INSTANCE = new VoxelShapeSplitHash();

    @Override
    public int hashCode(SplitVoxelShape o) {
        VSSplitAccess access = access(o);
        int result = Objects.hashCode(access.getAxis());
        result = 31 * result + VoxelShapePartHash.INSTANCE.hashCode(getPart(o));
        result = 31 * result + VoxelShapeHash.INSTANCE.hashCode(access.getShape());
        return result;
    }

    @Override
    public boolean equals(SplitVoxelShape a, SplitVoxelShape b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        }
        VSSplitAccess accessA = access(a);
        VSSplitAccess accessB = access(b);
        return Objects.equals(accessA.getAxis(), accessB.getAxis()) &&
                VoxelShapeHash.INSTANCE.equals(accessA.getShape(), accessB.getShape()) &&
                VoxelShapePartHash.INSTANCE.equals(getPart(a), getPart(b));
    }

    private static VSSplitAccess access(SplitVoxelShape a) {
        return (VSSplitAccess) a;
    }

    private static VoxelShapePart getPart(VoxelShape a) {
        return ((VoxelShapeAccess) a).getPart();
    }
}
