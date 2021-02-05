package malte0811.ferritecore.classloading;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.state.Property;
import net.minecraft.util.LazyValue;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

public class FastImmutableMapDefiner {
    private static final LazyValue<Method> DEFINE_CLASS = new LazyValue<>(() -> {
        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod(
                    "defineClass", String.class, byte[].class, int.class, int.class
            );
            defineClass.setAccessible(true);
            return defineClass;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    });
    private static final LazyValue<MethodHandle> MAKE_IMMUTABLE_FAST_MAP = new LazyValue<>(() -> {
        try {
            define("com.google.common.collect.FerriteCoreIterator");
            define("com.google.common.collect.FerriteCoreEntrySet");
            Class<?> map = define("com.google.common.collect.FerriteCoreImmutableMap");
            map.getField("numProperties").set(
                    null, (ToIntFunction<Object>) o -> ((FastMapStateHolder<?>) o).getStateMap().numProperties()
            );
            map.getField("getByStateAndKey").set(
                    null, (BiFunction<Object, Object, Comparable<?>>) (o, key) -> {
                        FastMapStateHolder<?> stateHolder = (FastMapStateHolder<?>) o;
                        return stateHolder.getStateMap().getValue(stateHolder.getStateIndex(), (Property<?>) key);
                    }
            );
            map.getField("entryByStateAndIndex").set(
                    null, (BiFunction<Object, Integer, Map.Entry<?, Comparable<?>>>) (o, key) -> {
                        FastMapStateHolder<?> stateHolder = (FastMapStateHolder<?>) o;
                        return stateHolder.getStateMap().getEntry(key, stateHolder.getStateIndex());
                    }
            );
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            return lookup.findConstructor(map, MethodType.methodType(
                    void.class, Object.class
            ));
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

    private static Class<?> define(String name) throws Exception {
        ClassLoader loaderToUse = ImmutableMap.class.getClassLoader();
        InputStream byteInput = FastImmutableMapDefiner.class.getResourceAsStream('/' + name.replace(
                '.',
                '/'
        ) + ".class");
        byte[] classBytes = new byte[byteInput.available()];
        final int bytesRead = byteInput.read(classBytes);
        Preconditions.checkState(bytesRead == classBytes.length);
        return (Class<?>) DEFINE_CLASS.getValue().invoke(loaderToUse, name, classBytes, 0, classBytes.length);
    }
}
