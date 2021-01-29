package malte0811.ferritecore.mixin.nopropertymap;

import com.google.common.collect.ImmutableList;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;

public class Config extends FerriteMixinConfig {
    @Override
    protected List<String> getAllMixins() {
        return ImmutableList.of("NoPropertyStateHolderMixin");
    }

    @Override
    protected boolean isEnabled(String mixin) {
        return FerriteConfig.PROPERTY_MAP.isEnabled();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
