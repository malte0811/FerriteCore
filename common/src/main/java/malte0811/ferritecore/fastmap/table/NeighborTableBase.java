package malte0811.ferritecore.fastmap.table;

import com.google.common.collect.Table;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NeighborTableBase<S> implements Table<Property<?>, Comparable<?>, S> {
    protected static final String ISSUES_URL = "https://github.com/malte0811/FerriteCore/issues";

    @Override
    public void clear() {
        crashOnModify();
    }

    @Override
    public final S put(@NotNull Property<?> rowKey, @NotNull Comparable<?> columnKey, @NotNull S value) {
        return crashOnModify();
    }

    @Override
    public final void putAll(@NotNull Table<? extends Property<?>, ? extends Comparable<?>, ? extends S> table) {
        crashOnModify();
    }

    @Override
    public final S remove(@Nullable Object rowKey, @Nullable Object columnKey) {
        return crashOnModify();
    }

    private static <T> T crashOnModify() {
        throw new UnsupportedOperationException(
                "A mod tried to modify the state neighbor table directly. Please report this at " + ISSUES_URL
        );
    }
}
