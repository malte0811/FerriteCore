package malte0811.ferritecore.fastmap;

import net.minecraft.state.Property;

public abstract class FastMapKey<T extends Comparable<T>> {
    private final PropertyIndexer<T> indexer;

    protected FastMapKey(Property<T> property) {
        this.indexer = PropertyIndexer.makeIndexer(property);
    }

    abstract T getValue(int mapIndex);

    abstract int replaceIn(int mapIndex, T newValue);

    abstract int toPartialMapIndex(Comparable<?> value);

    abstract int getFactorToNext();

    protected final int numValues() {
        return indexer.numValues();
    }

    protected final Property<T> getProperty() {
        return indexer.getProperty();
    }

    protected final int getInternalIndex(Comparable<?> value) {
        return indexer.toIndex((T) value);
    }

    protected final T byInternalIndex(int internalIndex) {
        return indexer.byIndex(internalIndex);
    }
}
