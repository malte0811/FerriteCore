package malte0811.ferritecore.mixin.blockstatecache;

import com.google.common.collect.ImmutableList;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;

import java.util.List;

public class Config extends FerriteMixinConfig {
    @Override
    protected List<String> getAllMixins() {
        return ImmutableList.of(
                "AbstractBlockStateMixin", "BlockStateCacheAccess", "VoxelShapeAccess", "VSArrayAccess",
                "VSPBitSetAccess", "VSPSplitAccess", "VSSplitAccess"
        );
    }

    @Override
    protected boolean isEnabled(String mixin) {
        return FerriteConfig.DEDUP_BLOCKSTATE_CACHE.isEnabled();
    }
}
