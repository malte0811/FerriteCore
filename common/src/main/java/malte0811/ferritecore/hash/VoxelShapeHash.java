package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import net.minecraft.util.math.shapes.SplitVoxelShape;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeArray;
import net.minecraft.util.math.shapes.VoxelShapeCube;

public class VoxelShapeHash implements Hash.Strategy<VoxelShape> {
    public static final VoxelShapeHash INSTANCE = new VoxelShapeHash();

    @Override
    public int hashCode(VoxelShape o) {
        if (o instanceof SplitVoxelShape) {
            return VoxelShapeSplitHash.INSTANCE.hashCode((SplitVoxelShape) o);
        } else if (o instanceof VoxelShapeArray) {
            return VoxelShapeArrayHash.INSTANCE.hashCode((VoxelShapeArray) o);
        } else if (o instanceof VoxelShapeCube) {
            return VoxelShapePartHash.INSTANCE.hashCode(((VoxelShapeAccess) o).getPart());
        } else {
            //TODO VSCube?
            return o.hashCode();
        }
    }

    @Override
    public boolean equals(VoxelShape a, VoxelShape b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else if (a.getClass() != b.getClass()) {
            return false;
        } else if (a instanceof SplitVoxelShape) {
            return VoxelShapeSplitHash.INSTANCE.equals((SplitVoxelShape) a, (SplitVoxelShape) b);
        } else if (a instanceof VoxelShapeArray) {
            return VoxelShapeArrayHash.INSTANCE.equals((VoxelShapeArray) a, (VoxelShapeArray) b);
        } else if (a instanceof VoxelShapeCube) {
            return VoxelShapePartHash.INSTANCE.equals(
                    ((VoxelShapeAccess) a).getPart(), ((VoxelShapeAccess) b).getPart()
            );
        } else {
            return a.equals(b);
        }
    }
}
