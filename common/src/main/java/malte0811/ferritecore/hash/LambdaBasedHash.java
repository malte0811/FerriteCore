package malte0811.ferritecore.hash;

import it.unimi.dsi.fastutil.Hash;

import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class LambdaBasedHash<T> implements Hash.Strategy<T> {
    private final ToIntFunction<T> hash;
    private final BiPredicate<T, T> equal;

    public LambdaBasedHash(ToIntFunction<T> hash, BiPredicate<T, T> equal) {
        this.hash = hash;
        this.equal = equal;
    }

    @Override
    public int hashCode(T o) {
        return hash.applyAsInt(o);
    }

    @Override
    public boolean equals(T a, T b) {
        return equal.test(a, b);
    }
}
