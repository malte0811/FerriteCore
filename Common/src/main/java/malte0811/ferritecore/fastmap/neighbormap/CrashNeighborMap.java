package malte0811.ferritecore.fastmap.neighbormap;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

/**
 * Singleton, which is assigned as the neighbor table for all blockstates by default. This makes it clear who is to be
 * blamed for any crashes, and also how to work around them.
 */
public class CrashNeighborMap<S> extends NeighborMapBase<S> {
    private static final CrashNeighborMap<?> INSTANCE = new CrashNeighborMap<>();

    @SuppressWarnings("unchecked")
    public static <S> CrashNeighborMap<S> getInstance() {
        return (CrashNeighborMap<S>) INSTANCE;
    }

    private CrashNeighborMap() {}

    @Override
    public int size() {
        return crashOnAccess();
    }

    @Override
    public boolean isEmpty() {
        return crashOnAccess();
    }

    @Override
    public boolean containsKey(Object key) {
        return crashOnAccess();
    }

    @Override
    public boolean containsValue(Object value) {
        return crashOnAccess();
    }

    @Override
    public S[] get(Object key) {
        return crashOnAccess();
    }

    @Override
    public @NotNull Set<Property<?>> keySet() {
        return crashOnAccess();
    }

    @Override
    public @NotNull Collection<S[]> values() {
        return crashOnAccess();
    }

    @Override
    public @NotNull Set<Entry<Property<?>, S[]>> entrySet() {
        return crashOnAccess();
    }

    private static <T> T crashOnAccess() {
        throw new UnsupportedOperationException(
                "A mod tried to access the state neighbor table directly. Please report this at " + ISSUES_URL +
                        ". As a temporary workaround you can enable \"populateNeighborTable\" in the FerriteCore config"
        );
    }
}
