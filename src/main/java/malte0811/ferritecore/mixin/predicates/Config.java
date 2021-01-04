package malte0811.ferritecore.mixin.predicates;

import com.google.common.collect.ImmutableList;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.FerriteMixinConfig;

import java.util.List;

public class Config extends FerriteMixinConfig {
    @Override
    protected List<String> getAllMixins() {
        return ImmutableList.of("AndConditionMixin", "OrConditionMixin", "PropertyValueConditionMixin");
    }

    @Override
    protected boolean isEnabled(String mixin) {
        return FerriteConfig.PREDICATES.isEnabled();
    }
}
