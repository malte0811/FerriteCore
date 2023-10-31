package malte0811.ferritecore.mixin.platform;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import malte0811.ferritecore.mixin.config.FerriteConfig.Option;
import malte0811.ferritecore.util.Constants;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConfigFileHandler {
    // Called reflectively from FerriteConfig
    public static void finish(List<Option> options) {
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

    private static CommentedFileConfig read(Path configPath) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                .sync()
                .preserveInsertionOrder()
                .build();
        configData.load();
        return configData;
    }
}
