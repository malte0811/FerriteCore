package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;
import malte0811.ferritecore.mixin.accessors.BitSetDVSAccess;
import malte0811.ferritecore.mixin.accessors.DiscreteVSAccess;
import malte0811.ferritecore.mixin.accessors.SubShapeAccess;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.Objects;

public class DiscreteVSHash implements Hash.Strategy<DiscreteVoxelShape> {
    public static final DiscreteVSHash INSTANCE = new DiscreteVSHash();

    @Override
    public int hashCode(DiscreteVoxelShape shape) {
        return hashCode((DiscreteVSAccess) shape);
    }

    public int hashCode(DiscreteVSAccess o) {
        int result = o.getXSize();
        result = 31 * result + o.getYSize();
        result = 31 * result + o.getZSize();
        if (o instanceof SubShapeAccess access) {
            result = 31 * result + access.getStartX();
            result = 31 * result + access.getStartY();
            result = 31 * result + access.getStartZ();
            result = 31 * result + access.getEndX();
            result = 31 * result + access.getEndY();
            result = 31 * result + access.getEndZ();
            result = 31 * result + hashCode((DiscreteVSAccess) access.getParent());
            return result;
        } else if (o instanceof BitSetDVSAccess access) {
            result = 31 * result + access.getXMin();
            result = 31 * result + access.getYMin();
            result = 31 * result + access.getZMin();
            result = 31 * result + access.getXMax();
            result = 31 * result + access.getYMax();
            result = 31 * result + access.getZMax();
            result = 31 * result + Objects.hashCode(access.getStorage());
            return result;
        } else {
            return 31 * result + Objects.hashCode(o);
        }
    }

    @Override
    public boolean equals(DiscreteVoxelShape a, DiscreteVoxelShape b) {
        return equals((DiscreteVSAccess) a, (DiscreteVSAccess) b);
    }

    public boolean equals(DiscreteVSAccess a, DiscreteVSAccess b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else if (a.getClass() != b.getClass()) {
            return false;
        }
        if (a.getXSize() != b.getXSize() || a.getYSize() != b.getYSize() || a.getZSize() != b.getZSize()) {
            return false;
        }
        if (a instanceof SubShapeAccess accessA) {
            SubShapeAccess accessB = (SubShapeAccess) b;
            return accessA.getEndX() == accessB.getEndX() &&
                    accessA.getEndY() == accessB.getEndY() &&
                    accessA.getEndZ() == accessB.getEndZ() &&
                    accessA.getStartX() == accessB.getStartX() &&
                    accessA.getStartY() == accessB.getStartY() &&
                    accessA.getStartZ() == accessB.getStartZ() &&
                    equals((DiscreteVSAccess) accessA.getParent(), (DiscreteVSAccess) accessB.getParent());
        } else if (a instanceof BitSetDVSAccess accessA) {
            BitSetDVSAccess accessB = (BitSetDVSAccess) b;
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
}
