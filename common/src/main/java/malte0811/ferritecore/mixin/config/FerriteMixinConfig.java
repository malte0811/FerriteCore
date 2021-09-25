package malte0811.ferritecore.mixin.config;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class FerriteMixinConfig implements IMixinConfigPlugin {
    protected static final Logger LOGGER = LogManager.getLogger("ferritecore-mixin");
    protected static final boolean HAS_HYDROGEN;
    private String prefix = null;
    @Nullable
    private final FerriteConfig.Option enableOption;
    private final boolean disableWithHydrogen;

    static {
        boolean hasHydrogen;
        try {
            // This does *not* load the class!
            MixinService.getService().getBytecodeProvider().getClassNode(
                    "me.jellysquid.mods.hydrogen.common.HydrogenMod");
            hasHydrogen = true;
        } catch (ClassNotFoundException | IOException e) {
            hasHydrogen = false;
        }
        HAS_HYDROGEN = hasHydrogen;
    }

    protected FerriteMixinConfig(@Nullable FerriteConfig.Option enableOption, boolean disableWithHydrogen) {
        this.enableOption = enableOption;
        this.disableWithHydrogen = disableWithHydrogen;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Preconditions.checkState(mixinClassName.startsWith(prefix), "Unexpected prefix on " + mixinClassName);
        if (enableOption != null && !enableOption.isEnabled()) {
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
    public String getRefMapperConfig() {return null;}

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {return null;}

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
