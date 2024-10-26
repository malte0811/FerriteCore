package malte0811.ferritecore.fastmap.neighbormap;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class NeighborMapBase<S> implements Map<Property<?>, S[]> {
    protected static final String ISSUES_URL = "https://github.com/malte0811/FerriteCore/issues";

    @Override
    public void clear() {
        crashOnModify();
    }

    @Override
    public @Nullable S[] put(Property<?> key, S[] value) {
        return crashOnModify();
    }

    @Override
    public S[] remove(Object key) {
        return crashOnModify();
    }

    @Override
    public void putAll(@NotNull Map<? extends Property<?>, ? extends S[]> m) {
        crashOnModify();
    }

    private static <T> T crashOnModify() {
        throw new UnsupportedOperationException(
                "A mod tried to modify the state neighbor table directly. Please report this at " + ISSUES_URL
        );
    }
}
