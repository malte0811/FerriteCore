package malte0811.ferritecore.mixin.fastmap;

import com.google.common.collect.ImmutableList;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;

import java.util.List;

public class Config extends FerriteMixinConfig {
    @Override
    protected List<String> getAllMixins() {
        return ImmutableList.of("FastMapStateHolderMixin");
    }

    @Override
    protected boolean isEnabled(String mixin) {
        return FerriteConfig.replaceNeighborTable();
    }
}
