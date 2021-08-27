package malte0811.ferritecore.mixin.platform;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import malte0811.ferritecore.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigFileHandler {
    // Called reflectively from FerriteConfig
    public static void finish(List<FerriteConfig.Option> options) throws IOException {
        Path config = Paths.get("config", Constants.MODID + ".mixin.properties");
        if (!Files.exists(config))
            Files.createFile(config);
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
}
