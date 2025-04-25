package malte0811.ferritecore.mixin.compactunihex;

import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;

// could probably also do the int ones but its probably not worth it
public class Config extends FerriteMixinConfig {
    public Config() {
        super(FerriteConfig.COMPACT_UNIHEX);
    }
}
