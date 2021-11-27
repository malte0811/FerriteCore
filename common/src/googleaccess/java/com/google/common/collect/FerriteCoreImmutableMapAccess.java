package com.google.common.collect;

import java.util.Map;

/**
 * Redeclares the package-private members of ImmutableMap as public, so it can be extended in other packages
 */
public abstract class FerriteCoreImmutableMapAccess<K, V> extends ImmutableMap<K, V> {

    public FerriteCoreImmutableMapAccess() {}

    @Override
    public abstract ImmutableSet<Map.Entry<K, V>> createEntrySet();

    @Override
    public abstract boolean isPartialView();

    @Override
    public abstract ImmutableSet<K> createKeySet();

    @Override
    public abstract ImmutableCollection<V> createValues();
}
