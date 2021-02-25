package malte0811.ferritecore.mixin.config;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.util.List;
import java.util.Set;

public abstract class FerriteMixinConfig implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("ferritecore-mixin");
    private static final boolean HAS_HYDROGEN = ClassInfo.forName("me.jellysquid.mods.hydrogen.common.HydrogenMod") != null;
    private String prefix = null;
    private final FerriteConfig.Option enableOption;
    private final boolean disableWithHydrogen;

    protected FerriteMixinConfig(FerriteConfig.Option enableOption, boolean disableWithHydrogen) {
        this.enableOption = enableOption;
        this.disableWithHydrogen = disableWithHydrogen;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Preconditions.checkState(mixinClassName.startsWith(prefix), "Unexpected prefix on " + mixinClassName);
        final String name = mixinClassName.substring(prefix.length());
        if (!enableOption.isEnabled()) {
            LOGGER.warn("Mixin " + mixinClassName + " is disabled by config");
            return false;
        } else if (HAS_HYDROGEN && disableWithHydrogen) {
            LOGGER.warn("Mixin " + mixinClassName + " is disabled as Hydrogen is installed");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        prefix = mixinPackage + ".";
    }

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
