package malte0811.ferritecore.mixin.config;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FerriteConfig {
    public static final Option NEIGHBOR_LOOKUP;
    public static final Option PROPERTY_MAP;
    public static final Option PREDICATES;
    public static final Option MRL_CACHE;
    public static final Option DEDUP_MULTIPART;
    public static final Option DEDUP_BLOCKSTATE_CACHE;
    public static final Option DEDUP_QUADS;
    public static final Option COMPACT_FAST_MAP;
    public static final Option POPULATE_NEIGHBOR_TABLE;
    public static final Option THREADING_DETECTOR;
    public static final Option MODEL_SIDES;

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
        DEDUP_QUADS = builder.createOption(
                "bakedQuadDeduplication",
                "Deduplicate vertex data of baked quads in the basic model implementations"
        );
        MODEL_SIDES = builder.createOption(
                "modelSides",
                "Use smaller data structures for \"simple\" models, especially models with few side-specific faces"
        );
        THREADING_DETECTOR = builder.createOptInOption(
                "useSmallThreadingDetector",
                "Replace objects used to detect multi-threaded access to chunks by a much smaller field. This option" +
                        " is disabled by default due to very rare and very hard-to-reproduce crashes, use at your own" +
                        " risk!"
        );
        COMPACT_FAST_MAP = builder.createOptInOption(
                "compactFastMap",
                "Use a slightly more compact, but also slightly slower representation for block states"
        );
        POPULATE_NEIGHBOR_TABLE = builder.createOptInOption(
                "populateNeighborTable",
                "Populate the neighbor table used by vanilla. Enabling this slightly increases memory usage, but" +
                        " can help with issues in the rare case where mods access it directly."
        );
        builder.finish();
    }

    public static class ConfigBuilder {
        private final List<Option> options = new ArrayList<>();

        public Option createOption(String name, String comment, Option... dependencies) {
            Option result = new Option(name, comment, true, dependencies);
            options.add(result);
            return result;
        }

        public Option createOptInOption(String name, String comment, Option... dependencies) {
            Option result = new Option(name, comment, false, dependencies);
            options.add(result);
            return result;
        }

        private void finish() {
            try {
                // This runs too early for arch's ExpectPlatform, so reflection it is
                Class<?> handler = Class.forName("malte0811.ferritecore.mixin.platform.ConfigFileHandler");
                Method finish = handler.getMethod("finish", List.class);
                finish.invoke(null, options);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Option {
        private final String name;
        private final String comment;
        private final boolean defaultValue;
        private final List<Option> dependencies;
        @Nullable
        private Boolean value;

        public Option(String name, String comment, boolean defaultValue, Option... dependencies) {
            this.name = name;
            this.comment = comment;
            this.defaultValue = defaultValue;
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

        public boolean getDefaultValue() {
            return defaultValue;
        }
    }
}
