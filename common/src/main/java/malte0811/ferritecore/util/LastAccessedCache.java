package malte0811.ferritecore.util;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;

import java.util.function.Function;

/**
 * A cache which checks if the last accessed value is being accessed again before checking the main map
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class LastAccessedCache<K, V> {
    private final Object2ObjectOpenCustomHashMap<K, V> mainMap;
    private final Function<K, V> createValue;
    private final Hash.Strategy<K> strategy;
    private Pair<K, V> lastAccessed;

    public LastAccessedCache(Hash.Strategy<K> strategy, Function<K, V> createValue) {
        this.strategy = strategy;
        this.mainMap = new Object2ObjectOpenCustomHashMap<>(strategy);
        this.createValue = createValue;
    }

    public Pair<K, V> get(K key) {
        final Pair<K, V> last = lastAccessed;
        if (last != null && strategy.equals(last.getFirst(), key)) {
            return last;
        } else {
            final V result = mainMap.computeIfAbsent(key, createValue);
            return lastAccessed = Pair.of(key, result);
        }
    }

    public void clear() {
        lastAccessed = null;
        mainMap.clear();
        mainMap.trim();
    }

    public int size() {
        return mainMap.size();
    }
}
