package malte0811.ferritecore.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class PredicateCache<T> {
    private final ThreadLocal<T> lastSource = new ThreadLocal<>();
    private final Map<T, Predicate<BlockState>> cache = new Object2ObjectOpenHashMap<>();

    public synchronized void getPre(T input, CallbackInfoReturnable<Predicate<BlockState>> result) {
        Predicate<BlockState> cached = cache.get(input);
        if (cached != null) {
            result.setReturnValue(cached);
        } else {
            lastSource.set(input);
        }
    }

    public synchronized void getPost(CallbackInfoReturnable<Predicate<BlockState>> result) {
        T source = lastSource.get();
        if (source != null) {
            Predicate<BlockState> cached = cache.putIfAbsent(source, result.getReturnValue());
            if (cached != null) {
                // Can happen if a predicate is calculated on two threads at once
                result.setReturnValue(cached);
            }
            lastSource.set(null);
        }
    }

    public synchronized void clear() {
        lastSource.set(null);
        cache.clear();
    }

    public synchronized Predicate<BlockState> get(T subPredicates, Function<T, Predicate<BlockState>> make) {
        return cache.computeIfAbsent(subPredicates, make);
    }
}
