package malte0811.ferritecore.mixin.config;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class FerriteMixinConfig implements IMixinConfigPlugin {
    protected static final Logger LOGGER = LogManager.getLogger("ferritecore-mixin");
    private static final boolean HAS_LITHIUM;
    private static final boolean HAS_ROADRUNNER;

    static {
        HAS_LITHIUM = hasClass("me.jellysquid.mods.lithium.common.LithiumMod");
        HAS_ROADRUNNER = hasClass("me.jellysquid.mods.lithium.common.RoadRunner");
    }

    private String prefix = null;
    private final FerriteConfig.Option enableOption;
    private final LithiumSupportState lithiumState;
    private final boolean optIn;

    protected FerriteMixinConfig(
            FerriteConfig.Option enableOption, LithiumSupportState lithiumCompat, boolean optIn
    ) {
        this.enableOption = enableOption;
        this.lithiumState = lithiumCompat;
        this.optIn = optIn;
    }

    protected FerriteMixinConfig(FerriteConfig.Option enableOption) {
        this(enableOption, LithiumSupportState.NO_CONFLICT, false);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Preconditions.checkState(mixinClassName.startsWith(prefix), "Unexpected prefix on " + mixinClassName);
        if (!enableOption.isEnabled()) {
            if (!optIn) {
                LOGGER.warn("Mixin " + mixinClassName + " is disabled by config");
            }
            return false;
        } else if (!this.lithiumState.shouldApply()) {
            LOGGER.warn("Mixin " + mixinClassName + " is disabled automatically as lithium is installed");
            return false;
        } else {
            if (optIn) {
                LOGGER.warn("Opt-in mixin {} is enabled by config", mixinClassName);
            }
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

    private static boolean hasClass(String name) {
        try {
            // This does *not* load the class!
            MixinService.getService().getBytecodeProvider().getClassNode(name);
            return true;
        } catch (ClassNotFoundException | IOException e) {
            return false;
        }
    }

    protected enum LithiumSupportState {
        NO_CONFLICT,
        INCOMPATIBLE,
        APPLY_IF_ROADRUNNER;

        private boolean shouldApply() {
            return switch (this) {
                case NO_CONFLICT -> true;
                case INCOMPATIBLE -> !HAS_LITHIUM;
                case APPLY_IF_ROADRUNNER -> !HAS_LITHIUM || HAS_ROADRUNNER;
            };
        }
    }
}
