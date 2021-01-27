package malte0811.ferritecore.mixin.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import malte0811.ferritecore.ModMain;
import me.shedaniel.architectury.annotations.ExpectPlatform;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class FerriteConfig {
    public static final Option NEIGHBOR_LOOKUP;
    public static final Option PROPERTY_MAP;
    public static final Option PREDICATES;
    public static final Option MRL_CACHE;
    public static final Option DEDUP_MULTIPART;
    public static final Option DEDUP_BLOCKSTATE_CACHE;

    static {
        ConfigBuilder builder = new ConfigBuilder();
        NEIGHBOR_LOOKUP = builder.createOption("replaceNeighborLookup", "Replace the blockstate neighbor table");
        PROPERTY_MAP = builder.createOption(
                "replacePropertyMap",
                "Do not store the properties of a state explicitly and read them" +
                        "from the replace neighbor table instead. Requires " + NEIGHBOR_LOOKUP.getName() + " to be enabled",
                NEIGHBOR_LOOKUP
        );
        PREDICATES = builder.createOption(
                "cacheMultipartPredicates",
                "Cache the predicate instances used in multipart models"
        );
        MRL_CACHE = builder.createOption(
                "modelResourceLocations",
                "Avoid creation of new strings when creating ModelResourceLocations"
        );
        DEDUP_MULTIPART = builder.createOption(
                "multipartDeduplication",
                "Do not create a new MultipartBakedModel instance for each block state using the same multipart" +
                        "model. Requires " + PREDICATES.getName() + " to be enabled",
                PREDICATES
        );
        DEDUP_BLOCKSTATE_CACHE = builder.createOption(
                "blockstateCacheDeduplication",
                "Deduplicate cached data for blockstates, most importantly collision and render shapes"
        );
        builder.finish();
    }

    public static class ConfigBuilder {
        private final List<Option> options = new ArrayList<>();

        public Option createOption(String name, String comment, Option... dependencies) {
            Option result = new Option(name, comment, dependencies);
            options.add(result);
            return result;
        }

        private void finish() {
            try {
                Path config = Paths.get("config", ModMain.MODID+".mixin.properties");
                if (!Files.exists(config))
                    Files.createFile(config);
                List<String> lines = Files.readAllLines(config);
                Object2BooleanMap<String> existingOptions = new Object2BooleanOpenHashMap<>();
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty() || line.charAt(0) == '#') {
                        continue;
                    }
                    final int indexOfEq = line.indexOf('=');
                    if (indexOfEq < 0) {
                        throw new RuntimeException("Invalid line "+line);
                    }
                    final String key = line.substring(0, indexOfEq).trim();
                    final String value = line.substring(indexOfEq + 1).trim();
                    existingOptions.put(key, Boolean.parseBoolean(value));
                }
                List<String> newLines = new ArrayList<>();
                Object2BooleanMap<String> actualOptions = new Object2BooleanOpenHashMap<>();
                for (Option o : options) {
                    final boolean value = existingOptions.getOrDefault(o.getName(), true);
                    actualOptions.put(o.getName(), value);
                    newLines.add("# "+o.getComment());
                    newLines.add(o.getName()+" = "+value);
                }
                for (Option o : options) {
                    o.set(actualOptions::getBoolean);
                }
                Files.write(config, newLines);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Option {
        private final String name;
        private final String comment;
        private final List<Option> dependencies;
        @Nullable
        private Boolean value;

        public Option(String name, String comment, Option... dependencies) {
            this.name = name;
            this.comment = comment;
            this.dependencies = Arrays.asList(dependencies);
        }

        public void set(Predicate<String> isEnabled) {
            final boolean enabled = isEnabled.test(getName());
            if (enabled) {
                for (Option dep : dependencies) {
                    if (!isEnabled.test(dep.getName())) {
                        throw new IllegalStateException(
                                getName() + " is enabled in the FerriteCore config, but " + dep.getName()
                                        + " is not. This is not supported!"
                        );
                    }
                }
            }
            this.value = enabled;
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
