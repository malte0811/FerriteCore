package malte0811.ferritecore.classloading;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.state.Property;
import net.minecraft.util.LazyValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Helper to define classes in the com.google.common.collect package without issues due to jar signing and classloaders
 * (the second one only seems to be an issue on Fabric, but the first one is a problem on both)
 */
public class FastImmutableMapDefiner {
    private static final Logger LOGGER = LogManager.getLogger("FerriteCore - class definer");
    public static String GOOGLE_ACCESS_PREFIX = "/googleaccess/";

    private static final LazyValue<Definer> DEFINE_CLASS = new LazyValue<>(() -> {
        try {
            // Try to create a Java 9+ style class definer
            // These are all public methods, but just don't exist in Java 8
            Method makePrivateLookup = MethodHandles.class.getMethod(
                    "privateLookupIn", Class.class, MethodHandles.Lookup.class
            );
            Object privateLookup = makePrivateLookup.invoke(null, ImmutableMap.class, MethodHandles.lookup());
            Method defineClass = MethodHandles.Lookup.class.getMethod("defineClass", byte[].class);
            LOGGER.info("Using Java 9+ class definer");
            return (bytes, name) -> (Class<?>) defineClass.invoke(privateLookup, (Object) bytes);
        } catch (Exception x) {
            try {
                // If that fails, try a Java 8 style definer
                Method defineClass = ClassLoader.class.getDeclaredMethod(
                        "defineClass", String.class, byte[].class, int.class, int.class
                );
                defineClass.setAccessible(true);
                ClassLoader loader = ImmutableMap.class.getClassLoader();
                LOGGER.info("Using Java 8 class definer");
                return (bytes, name) -> (Class<?>) defineClass.invoke(loader, name, bytes, 0, bytes.length);
            } catch (NoSuchMethodException e) {
                // Fail if neither works
                throw new RuntimeException(e);
            }
        }
    });

    /**
     * Creates a MethodHandle for the constructor of FastMapEntryImmutableMap which takes one argument, which has to be
     * an instance FastMapStateHolder. This also handles the necessary classloader acrobatics.
     */
    private static final LazyValue<MethodHandle> MAKE_IMMUTABLE_FAST_MAP = new LazyValue<>(() -> {
        try {
            // Load these in the app classloader!
            defineInAppClassloader("com.google.common.collect.FerriteCoreEntrySetAccess");
            defineInAppClassloader("com.google.common.collect.FerriteCoreImmutableMapAccess");
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
            return (ImmutableMap<Property<?>, Comparable<?>>) MAKE_IMMUTABLE_FAST_MAP.getValue().invoke(state);
        } catch (Error e) {
            throw e;
        } catch (Throwable x) {
            throw new RuntimeException(x);
        }
    }

    private static void defineInAppClassloader(String name) throws Exception {
        InputStream byteInput = FastImmutableMapDefiner.class.getResourceAsStream(
                GOOGLE_ACCESS_PREFIX + name.replace('.', '/') + ".class"
        );
        byte[] classBytes = new byte[byteInput.available()];
        final int bytesRead = byteInput.read(classBytes);
        Preconditions.checkState(bytesRead == classBytes.length);
        Class<?> loaded = DEFINE_CLASS.getValue().define(classBytes, name);
        Preconditions.checkState(loaded.getClassLoader() == ImmutableMap.class.getClassLoader());
    }

    private interface Definer {
        Class<?> define(byte[] bytes, String name) throws Exception;
    }
}
