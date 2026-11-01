package malte0811.ferritecore.mixin.platform;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import malte0811.ferritecore.mixin.config.FerriteConfig.Option;
import malte0811.ferritecore.mixin.config.IPlatformConfigHooks;
import malte0811.ferritecore.util.Constants;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// Instantiated reflectively in FerriteConfig under this exact name, so it must not move.
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
        // Two differences from NeoForge: LoadingModList is static on Forge, and Forge's ModInfo is a
        // record with no getConfigElement, so other mods declare their opt-outs under
        // [modproperties.<their modid>] with the "ferritecore:disabled_options" key rather than as a
        // top-level key of their [[mods]] entry.
        for (var mod : LoadingModList.getMods()) {
            Object overrides = mod.getModProperties().get(Constants.DISABLED_OVERRIDES_KEY);
            if (overrides == null) {
                continue;
            }
            if (overrides instanceof List<?> overrideList) {
                for (var override : overrideList) {
                    if (override instanceof String overrideName) {
                        disableOption.addOverride(overrideName, mod.getModId());
                    } else {
                        LOGGER.warn("Override list for {} contains non-string {}", mod.getModId(), override);
                    }
                }
            } else {
                LOGGER.warn("Overrides for {} are not a list: {}", mod.getModId(), overrides);
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
