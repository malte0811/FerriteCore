package malte0811.ferritecore.fastmap.table;

import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Singleton, which is assigned as the neighbor table for all blockstates by default. This makes it clear who is to be
 * blamed for any crashes, and also how to work around them.
 */
public class CrashNeighborTable<S> extends NeighborTableBase<S> {
    private static final CrashNeighborTable<?> INSTANCE = new CrashNeighborTable<>();

    @SuppressWarnings("unchecked")
    public static <S> CrashNeighborTable<S> getInstance() {
        return (CrashNeighborTable<S>) INSTANCE;
    }

    private CrashNeighborTable() {}

    @Override
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        return crashOnAccess();
    }

    @Override
    public boolean containsRow(@Nullable Object rowKey) {
        return crashOnAccess();
    }

    @Override
    public boolean containsColumn(@Nullable Object columnKey) {
        return crashOnAccess();
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        return crashOnAccess();
    }

    @Override
    public S get(@Nullable Object rowKey, @Nullable Object columnKey) {
        return crashOnAccess();
    }

    @Override
    public boolean isEmpty() {
        return crashOnAccess();
    }

    @Override
    public int size() {
        return crashOnAccess();
    }

    @Override
    public Map<Comparable<?>, S> row(@NotNull Property<?> rowKey) {
        return crashOnAccess();
    }

    @Override
    public Map<Property<?>, S> column(@NotNull Comparable<?> columnKey) {
        return crashOnAccess();
    }

    @Override
    public Set<Cell<Property<?>, Comparable<?>, S>> cellSet() {
        return crashOnAccess();
    }

    @Override
    public Set<Property<?>> rowKeySet() {
        return crashOnAccess();
    }

    @Override
    public Set<Comparable<?>> columnKeySet() {
        return crashOnAccess();
    }

    @Override
    public Collection<S> values() {
        return crashOnAccess();
    }

    @Override
    public Map<Property<?>, Map<Comparable<?>, S>> rowMap() {
        return crashOnAccess();
    }

    @Override
    public Map<Comparable<?>, Map<Property<?>, S>> columnMap() {
        return crashOnAccess();
    }

    private static <T> T crashOnAccess() {
        throw new UnsupportedOperationException(
                "A mod tried to access the state neighbor table directly. Please report this at " + ISSUES_URL +
                        ". As a temporary workaround you can enable \"populateNeighborTable\" in the FerriteCore config"
        );
    }
}
