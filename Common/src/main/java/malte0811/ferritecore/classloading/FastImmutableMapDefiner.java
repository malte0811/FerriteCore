package malte0811.ferritecore.classloading;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Supplier;

/**
 * Helper to define classes in the com.google.common.collect package without issues due to jar signing and classloaders
 * (the second one only seems to be an issue on Fabric, but the first one is a problem on both)
 */
public class FastImmutableMapDefiner {
    public static String GOOGLE_ACCESS_PREFIX = "/googleaccess/";
    public static String GOOGLE_ACCESS_SUFFIX = ".class_manual";

    private static final Supplier<Definer> DEFINE_CLASS = Suppliers.memoize(() -> {
        try {
            MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(
                    ImmutableMap.class, MethodHandles.lookup()
            );
            return (bytes, name) -> privateLookup.defineClass(bytes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * Creates a MethodHandle for the constructor of FastMapEntryImmutableMap which takes one argument, which has to be
     * an instance FastMapStateHolder. This also handles the necessary classloader acrobatics.
     */
    private static final Supplier<MethodHandle> MAKE_IMMUTABLE_FAST_MAP = Suppliers.memoize(() -> {
        try {
            // Load these in the app classloader!
            defineInAppClassloader("com.google.common.collect.FerriteCoreEntrySetAccess");
            defineInAppClassloader("com.google.common.collect.FerriteCoreImmutableMapAccess");
            defineInAppClassloader("com.google.common.collect.FerriteCoreImmutableCollectionAccess");
            // This lives in the transforming classloader, but must not be loaded before the previous classes are in
            // the app classloader!
            Class<?> map = Class.forName("malte0811.ferritecore.fastmap.immutable.FastMapEntryImmutableMap");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            return lookup.findConstructor(map, MethodType.methodType(void.class, FastMapStateHolder.class));
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    });

    public static ImmutableMap<Property<?>, Comparable<?>> makeMap(FastMapStateHolder<?> state) {
        try {
            return (ImmutableMap<Property<?>, Comparable<?>>) MAKE_IMMUTABLE_FAST_MAP.get().invoke(state);
        } catch (Error e) {
            throw e;
        } catch (Throwable x) {
            throw new RuntimeException(x);
        }
    }

    private static void defineInAppClassloader(String name) throws Exception {
        byte[] classBytes;
        try (InputStream byteInput = FastImmutableMapDefiner.class.getResourceAsStream(
                GOOGLE_ACCESS_PREFIX + name.replace('.', '/') + GOOGLE_ACCESS_SUFFIX
        )) {
            Preconditions.checkNotNull(byteInput, "Failed to find class bytes for " + name);
            classBytes = IOUtils.toByteArray(byteInput);
        }
        Class<?> loaded = DEFINE_CLASS.get().define(classBytes, name);
        Preconditions.checkState(loaded.getClassLoader() == ImmutableMap.class.getClassLoader());
    }

    private interface Definer {
        Class<?> define(byte[] bytes, String name) throws Exception;
    }
}
