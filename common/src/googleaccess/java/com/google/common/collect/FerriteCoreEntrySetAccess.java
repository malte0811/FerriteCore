package com.google.common.collect;

import java.util.Map;

/**
 * Same as {@link FerriteCoreImmutableMapAccess}
 */
public abstract class FerriteCoreEntrySetAccess<K, V> extends ImmutableSet<Map.Entry<K, V>> {

    public FerriteCoreEntrySetAccess() {}

    @Override
    public abstract boolean isPartialView();
}
