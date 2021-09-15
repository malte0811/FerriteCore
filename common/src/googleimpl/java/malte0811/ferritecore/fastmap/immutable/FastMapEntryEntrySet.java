package malte0811.ferritecore.fastmap.immutable;

import com.google.common.collect.FerriteCoreEntrySetAccess;
import com.google.common.collect.UnmodifiableIterator;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FastMapEntryEntrySet extends FerriteCoreEntrySetAccess<Property<?>, Comparable<?>> {
    private final FastMapStateHolder<?> viewedState;

    public FastMapEntryEntrySet(FastMapStateHolder<?> viewedState) {
        this.viewedState = viewedState;
    }

    @Override
    @NotNull
    public UnmodifiableIterator<Map.Entry<Property<?>, Comparable<?>>> iterator() {
        return new FastMapEntryIterator(viewedState);
    }

    @Override
    public int size() {
        return viewedState.getStateMap().numProperties();
    }

    @Override
    public boolean contains(@Nullable Object object) {
        if (!(object instanceof Map.Entry<?, ?> entry)) {
            return false;
        }
        Comparable<?> valueInMap = viewedState.getStateMap().getValue(viewedState.getStateIndex(), entry.getKey());
        return valueInMap != null && valueInMap.equals(((Map.Entry<?, ?>) object).getValue());
    }

    @Override
    public boolean isPartialView() {
        return false;
    }
}
