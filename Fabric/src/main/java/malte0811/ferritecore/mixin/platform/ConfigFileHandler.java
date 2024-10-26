package malte0811.ferritecore.mixin.platform;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.mixin.config.IPlatformConfigHooks;
import malte0811.ferritecore.util.Constants;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// Instantiated reflectively in FerriteConfig
public class ConfigFileHandler implements IPlatformConfigHooks {
    @Override
    public void readAndUpdateConfig(List<FerriteConfig.Option> options) throws IOException {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path config = configDir.resolve(Constants.MODID + ".mixin.properties");
        if (!Files.exists(config)) {
            try {
                Files.createDirectories(configDir);
            } catch (FileAlreadyExistsException x) {
                // Can happen if the config dir is a symlink, and possibly some other corner case on Windows? See #83
                if (!Files.isDirectory(configDir)) {
                    throw new IOException("Config dir exists, but is not a directory?", x);
                }
            }
            Files.createFile(config);
        }
        Properties propsInFile = new Properties();
        propsInFile.load(Files.newInputStream(config));
        Object2BooleanMap<String> existingOptions = new Object2BooleanOpenHashMap<>();
        for (String key : propsInFile.stringPropertyNames()) {
            existingOptions.put(key, Boolean.parseBoolean(propsInFile.getProperty(key)));
        }
        List<String> newLines = new ArrayList<>();
        Object2BooleanMap<String> actualOptions = new Object2BooleanOpenHashMap<>();
        // Write data back manually, we can't put comments on individual values using Properties
        for (FerriteConfig.Option o : options) {
            final boolean value = existingOptions.getOrDefault(o.getName(), o.getDefaultValue());
            actualOptions.put(o.getName(), value);
            newLines.add("# " + o.getComment());
            newLines.add(o.getName() + " = " + value);
        }
        for (FerriteConfig.Option o : options) {
            o.set(actualOptions::getBoolean);
        }
        Files.write(config, newLines);
    }

    @Override
    public void collectDisabledOverrides(OverrideCallback disableOption) {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            CustomValue customValue = mod.getMetadata().getCustomValues().get(Constants.DISABLED_OVERRIDES_KEY);
            if (customValue != null) {
                String modId = mod.getMetadata().getId();
                for (CustomValue disabledOptionValue : customValue.getAsArray()) {
                    disableOption.addOverride(disabledOptionValue.getAsString(), modId);
                }
            }
        }
    }
}
