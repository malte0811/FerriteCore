package malte0811.ferritecore.mixin.config;

import java.io.IOException;
import java.util.List;

public interface IPlatformConfigHooks {
    static IPlatformConfigHooks loadHooks() {
        try {
            Class<?> handler = Class.forName("malte0811.ferritecore.mixin.platform.ConfigFileHandler");
            return (IPlatformConfigHooks) handler.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void readAndUpdateConfig(List<FerriteConfig.Option> options) throws IOException;

    void collectDisabledOverrides(OverrideCallback disableOption);

    interface OverrideCallback {
        void addOverride(String option, String mod);
    }
}
