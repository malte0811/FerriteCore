package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeHash implements Hash.Strategy<VoxelShape> {
    public static final VoxelShapeHash INSTANCE = new VoxelShapeHash();

    @Override
    public int hashCode(VoxelShape o) {
        if (o instanceof SliceShape) {
            return VoxelShapeSplitHash.INSTANCE.hashCode((SliceShape) o);
        } else if (o instanceof ArrayVoxelShape) {
            return VoxelShapeArrayHash.INSTANCE.hashCode((ArrayVoxelShape) o);
        } else if (o instanceof CubeVoxelShape) {
            return VoxelShapePartHash.INSTANCE.hashCode(((VoxelShapeAccess) o).getShape());
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
        } else if (a instanceof SliceShape) {
            return VoxelShapeSplitHash.INSTANCE.equals((SliceShape) a, (SliceShape) b);
        } else if (a instanceof ArrayVoxelShape) {
            return VoxelShapeArrayHash.INSTANCE.equals((ArrayVoxelShape) a, (ArrayVoxelShape) b);
        } else if (a instanceof CubeVoxelShape) {
            return VoxelShapePartHash.INSTANCE.equals(
                    ((VoxelShapeAccess) a).getShape(), ((VoxelShapeAccess) b).getShape()
            );
        } else {
            return a.equals(b);
        }
    }
}
