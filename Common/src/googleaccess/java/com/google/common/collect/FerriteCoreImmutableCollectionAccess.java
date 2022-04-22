package com.google.common.collect;

/**
 * Same as {@link FerriteCoreImmutableMapAccess}
 */
public abstract class FerriteCoreImmutableCollectionAccess<T> extends ImmutableCollection<T> {
    public abstract boolean isPartialView();
}
