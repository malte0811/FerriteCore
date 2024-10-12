package malte0811.ferritecore.mixin.threaddetec;

import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;

public class Config extends FerriteMixinConfig {
    public Config() {
        super(FerriteConfig.THREADING_DETECTOR, LithiumSupportState.INCOMPATIBLE, true);
    }
}
