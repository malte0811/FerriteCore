package malte0811.ferritecore.mixin.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FerriteConfig {
    public static final Option NEIGHBOR_LOOKUP;
    public static final Option PROPERTY_MAP;
    public static final Option PREDICATES;
    public static final Option MRL_CACHE;

    static {
        ConfigBuilder builder = new ConfigBuilder();
        NEIGHBOR_LOOKUP = builder.createOption("replaceNeighborLookup", "Replace the blockstate neighbor table");
        PROPERTY_MAP = builder.createOption(
                "replacePropertyMap",
                "Do not store the properties of a state explicitly and read them" +
                        "from the replace neighbor table instead. Requires " + NEIGHBOR_LOOKUP.getName() + " to be enabled"
        );
        PREDICATES = builder.createOption(
                "cacheMultipartPredicates",
                "Cache the predicate instances used in multipart models"
        );
        MRL_CACHE = builder.createOption(
                "modelResourceLocations",
                "Avoid creation of new strings when creating ModelResourceLocations"
        );
        builder.finish();
        if (PROPERTY_MAP.isEnabled() && !NEIGHBOR_LOOKUP.isEnabled()) {
            throw new IllegalStateException(
                    PROPERTY_MAP.getName() + " is enabled in the FerriteCore config, but " +
                            NEIGHBOR_LOOKUP.getName() + " is not. This is not supported!"
            );
        }
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

    public static class ConfigBuilder {
        private final List<Option> options = new ArrayList<>();

        public Option createOption(String name, String comment) {
            Option result = new Option(name, comment);
            options.add(result);
            return result;
        }

        public void finish() {
            ConfigSpec spec = new ConfigSpec();
            for (Option o : options) {
                spec.define(o.getName(), true);
            }
            CommentedFileConfig configData = read(Paths.get("config", "ferritecore-mixin.toml"));
            for (Option o : options) {
                configData.setComment(o.getName(), o.getComment());
            }
            spec.correct(configData);
            configData.save();
            for (Option o : options) {
                o.value = configData.get(o.getName());
            }
        }
    }

    public static class Option {
        private final String name;
        private final String comment;
        @Nullable
        private Boolean value;

        public Option(String name, String comment) {
            this.name = name;
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public String getComment() {
            return comment;
        }

        public boolean isEnabled() {
            return Objects.requireNonNull(value);
        }
    }
}
