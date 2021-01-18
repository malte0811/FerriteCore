package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VSArrayAccess;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeArray;
import net.minecraft.util.math.shapes.VoxelShapePart;

import java.util.Objects;

public class VoxelShapeArrayHash implements Hash.Strategy<VoxelShapeArray> {
    public static final VoxelShapeArrayHash INSTANCE = new VoxelShapeArrayHash();

    @Override
    public int hashCode(VoxelShapeArray o) {
        VSArrayAccess access = access(o);
        return 31 * Objects.hash(access.getXPoints(), access.getYPoints(), access.getZPoints())
                + VoxelShapePartHash.INSTANCE.hashCode(getPart(o));
    }

    @Override
    public boolean equals(VoxelShapeArray a, VoxelShapeArray b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        }
        VSArrayAccess accessA = access(a);
        VSArrayAccess accessB = access(b);
        return Objects.equals(accessA.getXPoints(), accessB.getXPoints()) &&
                Objects.equals(accessA.getYPoints(), accessB.getYPoints()) &&
                Objects.equals(accessA.getZPoints(), accessB.getZPoints()) &&
                VoxelShapePartHash.INSTANCE.equals(getPart(a), getPart(b));
    }

    @SuppressWarnings("ConstantConditions")
    private static VSArrayAccess access(VoxelShapeArray a) {
        return (VSArrayAccess) (Object) a;
    }

    private static VoxelShapePart getPart(VoxelShape a) {
        return ((VoxelShapeAccess) a).getPart();
    }
}
