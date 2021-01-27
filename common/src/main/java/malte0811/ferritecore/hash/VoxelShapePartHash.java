package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VSPBitSetAccess;
import malte0811.ferritecore.mixin.blockstatecache.VSPSplitAccess;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.SubShape;

import java.util.Objects;

public class VoxelShapePartHash implements Hash.Strategy<DiscreteVoxelShape> {
    public static final VoxelShapePartHash INSTANCE = new VoxelShapePartHash();

    @Override
    public int hashCode(DiscreteVoxelShape o) {
        if (o instanceof SubShape) {
            VSPSplitAccess access = access((SubShape) o);
            int result = access.getStartX();
            result = 31 * result + access.getStartY();
            result = 31 * result + access.getStartZ();
            result = 31 * result + access.getEndX();
            result = 31 * result + access.getEndY();
            result = 31 * result + access.getEndZ();
            result = 31 * result + hashCode(access.getParent());
            return result;
        } else if (o instanceof BitSetDiscreteVoxelShape) {
            VSPBitSetAccess access = access((BitSetDiscreteVoxelShape) o);
            int result = access.getXMin();
            result = 31 * result + access.getYMin();
            result = 31 * result + access.getZMin();
            result = 31 * result + access.getXMax();
            result = 31 * result + access.getYMax();
            result = 31 * result + access.getZMax();
            result = 31 * result + Objects.hashCode(access.getStorage());
            return result;
        } else {
            return Objects.hashCode(o);
        }
    }

    @Override
    public boolean equals(DiscreteVoxelShape a, DiscreteVoxelShape b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else if (a.getClass() != b.getClass()) {
            return false;
        } else if (a instanceof SubShape) {
            VSPSplitAccess accessA = access((SubShape) a);
            VSPSplitAccess accessB = access((SubShape) b);
            return accessA.getEndX() == accessB.getEndX() &&
                    accessA.getEndY() == accessB.getEndY() &&
                    accessA.getEndZ() == accessB.getEndZ() &&
                    accessA.getStartX() == accessB.getStartX() &&
                    accessA.getStartY() == accessB.getStartY() &&
                    accessA.getStartZ() == accessB.getStartZ() &&
                    equals(accessA.getParent(), accessB.getParent());
        } else if (a instanceof BitSetDiscreteVoxelShape) {
            VSPBitSetAccess accessA = access((BitSetDiscreteVoxelShape) a);
            VSPBitSetAccess accessB = access((BitSetDiscreteVoxelShape) b);
            return accessA.getXMax() == accessB.getXMax() &&
                    accessA.getYMax() == accessB.getYMax() &&
                    accessA.getZMax() == accessB.getZMax() &&
                    accessA.getXMin() == accessB.getXMin() &&
                    accessA.getYMin() == accessB.getYMin() &&
                    accessA.getZMin() == accessB.getZMin() &&
                    accessA.getStorage().equals(accessB.getStorage());
        } else {
            return a.equals(b);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static VSPSplitAccess access(SubShape part) {
        return (VSPSplitAccess) (Object) part;
    }

    @SuppressWarnings("ConstantConditions")
    private static VSPBitSetAccess access(BitSetDiscreteVoxelShape part) {
        return (VSPBitSetAccess) (Object) part;
    }
}
