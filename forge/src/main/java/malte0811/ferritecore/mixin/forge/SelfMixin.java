package malte0811.ferritecore.mixin.forge;

import malte0811.ferritecore.ModMainForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ModMainForge.class)
public class SelfMixin {
    /**
     * @reason Detect Mixins when loaded
     * @author malte0811
     */
    @Overwrite(remap = false)
    private static boolean hasMixins() {
        return true;
    }
}
