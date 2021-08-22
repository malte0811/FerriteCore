package malte0811.ferritecore.ducks;

import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

// This should be an accessor Mixin, but some part of the toolchain does not handle setters for fields of inner classes
// properly
public interface BlockStateCacheAccess {
    VoxelShape getCollisionShape();

    void setCollisionShape(VoxelShape newShape);

    VoxelShape[] getOcclusionShapes();

    void setOcclusionShapes(@Nullable VoxelShape[] newShapes);
}
