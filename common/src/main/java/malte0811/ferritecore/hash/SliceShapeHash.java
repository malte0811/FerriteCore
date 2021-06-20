package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.SliceShapeAccess;

import java.util.Objects;

public class SliceShapeHash implements Hash.Strategy<SliceShapeAccess> {
    public static final SliceShapeHash INSTANCE = new SliceShapeHash();

    @Override
    public int hashCode(SliceShapeAccess o) {
        int result = Objects.hashCode(o.getAxis());
        result = 31 * result + VoxelShapePartHash.INSTANCE.hashCode(o.getShape());
        result = 31 * result + VoxelShapeHash.INSTANCE.hashCode(o.getDelegate());
        return result;
    }

    @Override
    public boolean equals(SliceShapeAccess a, SliceShapeAccess b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        }
        return Objects.equals(a.getAxis(), b.getAxis()) &&
                VoxelShapeHash.INSTANCE.equals(a.getDelegate(), b.getDelegate()) &&
                VoxelShapePartHash.INSTANCE.equals(a.getShape(), b.getShape());
    }
}
