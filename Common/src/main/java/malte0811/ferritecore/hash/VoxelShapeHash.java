package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.accessors.ArrayVSAccess;
import malte0811.ferritecore.mixin.accessors.SliceShapeAccess;
import malte0811.ferritecore.mixin.accessors.VoxelShapeAccess;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeHash implements Hash.Strategy<VoxelShape> {
    public static final VoxelShapeHash INSTANCE = new VoxelShapeHash();

    @Override
    public int hashCode(VoxelShape o) {
        return hashCode((VoxelShapeAccess) o);
    }

    public int hashCode(VoxelShapeAccess o) {
        if (o instanceof SliceShapeAccess access) {
            return SliceShapeHash.INSTANCE.hashCode(access);
        } else if (o instanceof ArrayVSAccess access) {
            return ArrayVoxelShapeHash.INSTANCE.hashCode(access);
        } else if (isCubeShape(o)) {
            return DiscreteVSHash.INSTANCE.hashCode(o.getShape());
        } else {
            return o.hashCode();
        }
    }

    @Override
    public boolean equals(VoxelShape a, VoxelShape b) {
        return equals((VoxelShapeAccess) a, (VoxelShapeAccess) b);
    }

    public boolean equals(VoxelShapeAccess a, VoxelShapeAccess b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else if (a.getClass() != b.getClass()) {
            return false;
        } else if (a instanceof SliceShapeAccess accessA) {
            return SliceShapeHash.INSTANCE.equals(accessA, (SliceShapeAccess) b);
        } else if (a instanceof ArrayVSAccess accessA) {
            return ArrayVoxelShapeHash.INSTANCE.equals(accessA, (ArrayVSAccess) b);
        } else if (isCubeShape(a)) {
            return DiscreteVSHash.INSTANCE.equals(a.getShape(), b.getShape());
        } else {
            return a.equals(b);
        }
    }

    private boolean isCubeShape(Object o) {
        return o instanceof CubeVoxelShape;
    }
}
