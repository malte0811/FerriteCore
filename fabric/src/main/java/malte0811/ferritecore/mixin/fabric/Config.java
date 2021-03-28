package malte0811.ferritecore.mixin.fabric;

import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;

import java.util.Collections;
import java.util.List;

public class Config extends FerriteMixinConfig {
    public Config() {
        super(null, false);
    }

    @Override
    public List<String> getMixins() {
        if (HAS_HYDROGEN && FerriteConfig.NEIGHBOR_LOOKUP.isEnabled()) {
            LOGGER.warn("Adding Mixin to disable Hydrogen's MixinState#postCreateWithTable");
            return Collections.singletonList("HydrogenStateholderMixin");
        } else {
            return super.getMixins();
        }
    }
}
