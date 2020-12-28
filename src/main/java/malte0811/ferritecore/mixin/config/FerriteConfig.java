package malte0811.ferritecore.mixin.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FerriteConfig {
    private static final String NEIGHBOR_LOOKUP = "replaceNeighborLookup";
    //TODO actually split
    private static final String PROPERTY_MAP = "replacePropertyMap";
    private static final String PREDICATES = "cacheMultipartPredicates";
    private static final Config CONFIG;

    static {
        ConfigSpec spec = new ConfigSpec();
        spec.define(NEIGHBOR_LOOKUP, true);
        spec.define(PROPERTY_MAP, true);
        spec.define(PREDICATES, true);
        CommentedFileConfig configData = read(Paths.get("config", "ferritecore-mixin.toml"));
        configData.setComment(NEIGHBOR_LOOKUP, "Replace the blockstate neighbor table");
        configData.setComment(PROPERTY_MAP, "Do not store the properties of a state explicitly and read them" +
                "from the replace neighbor table instead. Requires " + NEIGHBOR_LOOKUP + " to be enabled");
        configData.setComment(PREDICATES, "Cache the predicate instances used in multipart models");
        spec.correct(configData);
        configData.save();
        CONFIG = configData;
    }

    private static CommentedFileConfig read(Path configPath) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                .sync()
                .preserveInsertionOrder()
                .build();
        try {
            configData.load();
        } catch (ParsingException ex) {
            throw new RuntimeException("Failed to load config " + configPath, ex);
        }
        return configData;
    }

    public static boolean replaceNeighborTable() {
        return CONFIG.get(NEIGHBOR_LOOKUP);
    }

    public static boolean cachePredicates() {
        return CONFIG.get(PREDICATES);
    }

    public static boolean noPropertyState() {
        return CONFIG.<Boolean>get(PROPERTY_MAP) && CONFIG.<Boolean>get(NEIGHBOR_LOOKUP);
    }
}
