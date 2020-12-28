package malte0811.ferritecore.mixin.config;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class FerriteMixinConfig implements IMixinConfigPlugin {
    private static final String MIXIN_PREFIX = "malte0811.ferritecore.mixin.";
    private static final Logger LOGGER = LogManager.getLogger("ferritecore-mixin");

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Preconditions.checkState(mixinClassName.startsWith(MIXIN_PREFIX), "Unexpected prefix on " + mixinClassName);
        final String name = mixinClassName.substring(MIXIN_PREFIX.length());
        boolean result;
        switch (name) {
            case "OrConditionMixin":
            case "AndConditionMixin":
            case "PropertyValueConditionMixin":
                result = FerriteConfig.cachePredicates();
                break;
            case "FastMapStateHolderMixin":
                result = FerriteConfig.replaceNeighborTable();
                break;
            case "NoPropertyStateHolderMixin":
                result = FerriteConfig.noPropertyState();
                break;
            default:
                throw new RuntimeException("Unknown mixin: " + name);
        }
        if (!result) {
            LOGGER.info("Mixin {} is disabled due to config settings", mixinClassName);
        }
        return result;
    }

    @Override
    public void onLoad(String mixinPackage) {
        Preconditions.checkState(MIXIN_PREFIX.equals(mixinPackage + "."));
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
