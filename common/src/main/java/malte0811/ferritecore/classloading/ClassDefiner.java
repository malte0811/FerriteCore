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

public class ClassDefiner {
    private static final LazyValue<MethodHandle> MAKE_IMMUTABLE_FAST_MAP = new LazyValue<>(() -> {
        try {
            define("com.google.common.collect.FerriteCoreIterator");
            define("com.google.common.collect.FerriteCoreEntrySet");
            Class<?> map = define("com.google.common.collect.FerriteCoreImmutableMap");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            //int numProperties, Function<Object, V> getValue, IntFunction<Entry<K, V>> getIth
            return lookup.findConstructor(map, MethodType.methodType(
                    void.class, int.class, Function.class, IntFunction.class
            ));
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    });

    public static <K, V> ImmutableMap<K, V> makeMap(
            int numProperties, Function<Object, V> getValue, IntFunction<Map.Entry<K, V>> getIth
    ) throws Throwable {
        return (ImmutableMap<K, V>) MAKE_IMMUTABLE_FAST_MAP.getValue().invoke(numProperties, getValue, getIth);
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
