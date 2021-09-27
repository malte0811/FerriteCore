package malte0811.ferritecore.fastmap.immutable;

import com.google.common.collect.UnmodifiableIterator;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public class FastMapEntryIterator extends UnmodifiableIterator<Map.Entry<Property<?>, Comparable<?>>> {
    private final FastMapStateHolder<?> viewedState;
    private int currentIndex = 0;

    public FastMapEntryIterator(FastMapStateHolder<?> viewedState) {
        this.viewedState = viewedState;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < viewedState.getStateMap().numProperties();
    }

    @Override
    public Map.Entry<Property<?>, Comparable<?>> next() {
        Map.Entry<Property<?>, Comparable<?>> next = viewedState.getStateMap().getEntry(
                currentIndex, viewedState.getStateIndex()
        );
        ++currentIndex;
        return next;
    }
}
