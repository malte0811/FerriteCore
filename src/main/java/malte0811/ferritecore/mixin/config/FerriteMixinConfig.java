package malte0811.ferritecore.mixin.config;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public abstract class FerriteMixinConfig implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogManager.getLogger("ferritecore-mixin");
    private String prefix = null;
    private List<String> providedMixins;

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Preconditions.checkState(mixinClassName.startsWith(prefix), "Unexpected prefix on " + mixinClassName);
        final String name = mixinClassName.substring(prefix.length());
        Preconditions.checkState(
                providedMixins.contains(name),
                "Unexpected Mixin: " + name + " (" + mixinClassName + ")"
        );
        final boolean result = isEnabled(name);
        if (!result) {
            LOGGER.debug("Mixin " + mixinClassName + " is disabled by config");
        }
        return result;
    }

    protected abstract List<String> getAllMixins();

    protected abstract boolean isEnabled(String mixin);

    @Override
    public void onLoad(String mixinPackage) {
        prefix = mixinPackage + ".";
        providedMixins = getAllMixins();
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
