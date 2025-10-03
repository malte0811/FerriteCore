package malte0811.ferritecore.mixin.platform;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import malte0811.ferritecore.mixin.config.FerriteConfig.Option;
import malte0811.ferritecore.mixin.config.IPlatformConfigHooks;
import malte0811.ferritecore.util.Constants;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// Instantiated reflectively in FerriteConfig
public class ConfigFileHandler implements IPlatformConfigHooks {
    private static final Logger LOGGER = LoggerFactory.getLogger("ferritecore-overrides");

    @Override
    public void readAndUpdateConfig(List<Option> options) throws IOException {
        ConfigSpec spec = new ConfigSpec();
        for (Option o : options) {
            spec.define(o.getName(), o.getDefaultValue());
        }
        CommentedFileConfig configData = read(
                FMLPaths.CONFIGDIR.get().resolve(Constants.MODID + "-mixin.toml")
        );
        for (Option o : options) {
            configData.setComment(o.getName(), o.getComment());
        }
        spec.correct(configData);
        configData.save();
        for (Option o : options) {
            o.set(configData::get);
        }
    }

    @Override
    public void collectDisabledOverrides(OverrideCallback disableOption) {
        for (var mod : FMLLoader.getCurrent().getLoadingModList().getMods()) {
            var maybeOverrides = mod.getConfigElement(Constants.DISABLED_OVERRIDES_KEY);
            if (maybeOverrides.isEmpty()) { continue; }
            if (maybeOverrides.get() instanceof List<?> overrides) {
                for (var override : overrides) {
                    if (override instanceof String overrideName) {
                        disableOption.addOverride(overrideName, mod.getModId());
                    } else {
                        LOGGER.warn("Override list for {} contains non-string {}", mod.getModId(), override);
                    }
                }
            } else {
                LOGGER.warn("Overrides for {} are not a list: {}", mod.getModId(), maybeOverrides.get());
            }
        }
    }

    private static CommentedFileConfig read(Path configPath) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                .sync()
                .preserveInsertionOrder()
                .build();
        configData.load();
        return configData;
    }
}
