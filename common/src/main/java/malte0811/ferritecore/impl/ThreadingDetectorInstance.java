package malte0811.ferritecore.impl;

import malte0811.ferritecore.util.SmallThreadingDetector;
import net.minecraft.world.level.chunk.PalettedContainer;

// Class is not loaded unless threaddetec mixin is active
public class ThreadingDetectorInstance {
    public static final SmallThreadingDetector PALETTED_CONTAINER_DETECTOR;

    static {
        try {
            //noinspection JavaReflectionMemberAccess (Field is added by Mixin)
            var ownerField = PalettedContainer.class.getDeclaredField("ownerThread");
            PALETTED_CONTAINER_DETECTOR = new SmallThreadingDetector(ownerField, "PalettedContainer");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
