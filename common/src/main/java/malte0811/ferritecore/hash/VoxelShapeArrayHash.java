package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VSArrayAccess;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

public class VoxelShapeArrayHash implements Hash.Strategy<ArrayVoxelShape> {
    public static final VoxelShapeArrayHash INSTANCE = new VoxelShapeArrayHash();

    @Override
    public int hashCode(ArrayVoxelShape o) {
        VSArrayAccess access = access(o);
        return 31 * Objects.hash(access.getXs(), access.getYs(), access.getZs())
                + VoxelShapePartHash.INSTANCE.hashCode(getPart(o));
    }

    @Override
    public boolean equals(ArrayVoxelShape a, ArrayVoxelShape b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        }
        VSArrayAccess accessA = access(a);
        VSArrayAccess accessB = access(b);
        return Objects.equals(accessA.getXs(), accessB.getXs()) &&
                Objects.equals(accessA.getYs(), accessB.getYs()) &&
                Objects.equals(accessA.getZs(), accessB.getZs()) &&
                VoxelShapePartHash.INSTANCE.equals(getPart(a), getPart(b));
    }

    @SuppressWarnings("ConstantConditions")
    private static VSArrayAccess access(ArrayVoxelShape a) {
        return (VSArrayAccess) (Object) a;
    }

    private static DiscreteVoxelShape getPart(VoxelShape a) {
        return ((VoxelShapeAccess) a).getShape();
    }
}
