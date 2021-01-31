package malte0811.ferritecore.classloading;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.LazyValue;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

public class ClassDefiner {
    private static final LazyValue<MethodHandle> MAKE_IMMUTABLE_FAST_MAP = new LazyValue<>(() -> {
        try {
            define("com.google.common.collect.FerriteCoreIterator");
            define("com.google.common.collect.FerriteCoreEntrySet");
            Class<?> map = define("com.google.common.collect.FerriteCoreImmutableMap");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            // Function is:
            // Function<Object, V>: Map#get
            // IntFunction<Entry<K, V>>: get i-th entry of the map
            // IntSupplier: Map#size
            return lookup.findConstructor(map, MethodType.methodType(
                    void.class, Function.class
            ));
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    });

    public static <K, V, F extends Function<Object, V> & IntFunction<Map.Entry<K, V>> & IntSupplier>
    ImmutableMap<K, V> makeMap(F mapAccess) throws Throwable {
        return (ImmutableMap<K, V>) MAKE_IMMUTABLE_FAST_MAP.getValue().invoke(mapAccess);
    }

    private static Class<?> define(String name) throws Exception {
        ClassLoader loaderToUse = ImmutableMap.class.getClassLoader();
        InputStream byteInput = ClassDefiner.class.getResourceAsStream('/' + name.replace('.', '/') + ".class");
        byte[] classBytes = new byte[byteInput.available()];
        final int bytesRead = byteInput.read(classBytes);
        Preconditions.checkState(bytesRead == classBytes.length);
        Method defineClass = ClassLoader.class.getDeclaredMethod(
                "defineClass", String.class, byte[].class, int.class, int.class
        );
        defineClass.setAccessible(true);
        return (Class<?>) defineClass.invoke(loaderToUse, name, classBytes, 0, classBytes.length);
    }
}
