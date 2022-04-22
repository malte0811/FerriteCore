package malte0811.ferritecore.mixin.blockstatecache;

import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;

public class Config extends FerriteMixinConfig {
    public Config() {
        super(FerriteConfig.DEDUP_BLOCKSTATE_CACHE);
    }
}
