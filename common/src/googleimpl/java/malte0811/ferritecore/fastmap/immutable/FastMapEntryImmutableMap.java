package malte0811.ferritecore.fastmap.immutable;

import com.google.common.collect.FerriteCoreImmutableMapAccess;
import com.google.common.collect.ImmutableSet;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.state.Property;
import org.jetbrains.annotations.Nullable;

public class FastMapEntryImmutableMap extends FerriteCoreImmutableMapAccess<Property<?>, Comparable<?>> {
    private final FastMapStateHolder<?> viewedState;

    public FastMapEntryImmutableMap(FastMapStateHolder<?> viewedState) {
        this.viewedState = viewedState;
    }

    @Override
    public int size() {
        return viewedState.getStateMap().numProperties();
    }

    @Override
    public Comparable<?> get(@Nullable Object key) {
        return viewedState.getStateMap().getValue(viewedState.getStateIndex(), key);
    }

    @Override
    public ImmutableSet<Entry<Property<?>, Comparable<?>>> createEntrySet() {
        return new FastMapEntryEntrySet(viewedState);
    }

    @Override
    public ImmutableSet<Entry<Property<?>, Comparable<?>>> entrySet() {
        return new FastMapEntryEntrySet(viewedState);
    }

    @Override
    public boolean isPartialView() {
        return false;
    }
}
