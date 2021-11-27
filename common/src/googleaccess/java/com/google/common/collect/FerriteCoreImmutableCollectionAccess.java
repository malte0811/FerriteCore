package com.google.common.collect;

import org.jetbrains.annotations.Nullable;

/**
 * Same as {@link FerriteCoreImmutableMapAccess}
 */
public abstract class FerriteCoreImmutableCollectionAccess<T> extends ImmutableCollection<T> {
    public abstract boolean isPartialView();
}
