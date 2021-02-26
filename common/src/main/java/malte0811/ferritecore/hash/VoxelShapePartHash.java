package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.blockstatecache.VSPBitSetAccess;
import malte0811.ferritecore.mixin.blockstatecache.VSPSplitAccess;
import malte0811.ferritecore.mixin.blockstatecache.VSPartAccess;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.PartSplitVoxelShape;
import net.minecraft.util.math.shapes.VoxelShapePart;

import java.util.Objects;

public class VoxelShapePartHash implements Hash.Strategy<VoxelShapePart> {
    public static final VoxelShapePartHash INSTANCE = new VoxelShapePartHash();

    @Override
    public int hashCode(VoxelShapePart o) {
        VSPartAccess generalAccess = (VSPartAccess) o;
        int result = generalAccess.getXSize();
        result = 31 * result + generalAccess.getYSize();
        result = 31 * result + generalAccess.getZSize();
        if (o instanceof PartSplitVoxelShape) {
            VSPSplitAccess access = access((PartSplitVoxelShape) o);
            result = 31 * result + access.getStartX();
            result = 31 * result + access.getStartY();
            result = 31 * result + access.getStartZ();
            result = 31 * result + access.getEndX();
            result = 31 * result + access.getEndY();
            result = 31 * result + access.getEndZ();
            result = 31 * result + hashCode(access.getPart());
            return result;
        } else if (o instanceof BitSetVoxelShapePart) {
            VSPBitSetAccess access = access((BitSetVoxelShapePart) o);
            result = 31 * result + access.getStartX();
            result = 31 * result + access.getStartY();
            result = 31 * result + access.getStartZ();
            result = 31 * result + access.getEndX();
            result = 31 * result + access.getEndY();
            result = 31 * result + access.getEndZ();
            result = 31 * result + Objects.hashCode(access.getBitSet());
            return result;
        } else {
            return 31 * result + Objects.hashCode(o);
        }
    }

    @Override
    public boolean equals(VoxelShapePart a, VoxelShapePart b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else if (a.getClass() != b.getClass()) {
            return false;
        }
        VSPartAccess genAccessA = (VSPartAccess) a;
        VSPartAccess genAccessB = (VSPartAccess) b;
        if (genAccessA.getXSize() != genAccessB.getXSize() ||
                genAccessA.getYSize() != genAccessB.getYSize() ||
                genAccessA.getZSize() != genAccessB.getZSize()
        ) {
            return false;
        }
        if (a instanceof PartSplitVoxelShape) {
            VSPSplitAccess accessA = access((PartSplitVoxelShape) a);
            VSPSplitAccess accessB = access((PartSplitVoxelShape) b);
            return accessA.getEndX() == accessB.getEndX() &&
                    accessA.getEndY() == accessB.getEndY() &&
                    accessA.getEndZ() == accessB.getEndZ() &&
                    accessA.getStartX() == accessB.getStartX() &&
                    accessA.getStartY() == accessB.getStartY() &&
                    accessA.getStartZ() == accessB.getStartZ() &&
                    equals(accessA.getPart(), accessB.getPart());
        } else if (a instanceof BitSetVoxelShapePart) {
            VSPBitSetAccess accessA = access((BitSetVoxelShapePart) a);
            VSPBitSetAccess accessB = access((BitSetVoxelShapePart) b);
            return accessA.getEndX() == accessB.getEndX() &&
                    accessA.getEndY() == accessB.getEndY() &&
                    accessA.getEndZ() == accessB.getEndZ() &&
                    accessA.getStartX() == accessB.getStartX() &&
                    accessA.getStartY() == accessB.getStartY() &&
                    accessA.getStartZ() == accessB.getStartZ() &&
                    accessA.getBitSet().equals(accessB.getBitSet());
        } else {
            return a.equals(b);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static VSPSplitAccess access(PartSplitVoxelShape part) {
        return (VSPSplitAccess) (Object) part;
    }

    @SuppressWarnings("ConstantConditions")
    private static VSPBitSetAccess access(BitSetVoxelShapePart part) {
        return (VSPBitSetAccess) (Object) part;
    }
}
