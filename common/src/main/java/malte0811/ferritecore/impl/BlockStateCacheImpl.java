package malte0811.ferritecore.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import malte0811.ferritecore.hash.VoxelShapeArrayHash;
import malte0811.ferritecore.hash.VoxelShapeHash;
import malte0811.ferritecore.mixin.blockstatecache.BlockStateCacheAccess;
import malte0811.ferritecore.mixin.blockstatecache.VSArrayAccess;
import malte0811.ferritecore.mixin.blockstatecache.VSSplitAccess;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import malte0811.ferritecore.util.Constants;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.shapes.SplitVoxelShape;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeArray;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.block.AbstractBlock.AbstractBlockState;

public class BlockStateCacheImpl {
    public static final Map<VoxelShapeArray, VoxelShapeArray> CACHE_COLLIDE = new Object2ObjectOpenCustomHashMap<>(
            VoxelShapeArrayHash.INSTANCE
    );
    // Maps a shape to the "canonical instance" of that shape and its side projections
    public static final Map<VoxelShape, Pair<VoxelShape, VoxelShape[]>> CACHE_PROJECT =
            new Object2ObjectOpenCustomHashMap<>(VoxelShapeHash.INSTANCE);

    // Get the cache from a blockstate. Mixin does not handle private inner classes too well, so method handles and
    // manual remapping it is
    private static final LazyValue<Function<AbstractBlockState, BlockStateCacheAccess>> GET_CACHE =
            new LazyValue<>(() -> {
                try {
                    Field cacheField = AbstractBlockState.class.getDeclaredField(Constants.blockstateCacheFieldName);
                    cacheField.setAccessible(true);
                    MethodHandle getter = MethodHandles.lookup().unreflectGetter(cacheField);
                    return state -> {
                        try {
                            return (BlockStateCacheAccess) getter.invoke(state);
                        } catch (Throwable throwable) {
                            throw new RuntimeException(throwable);
                        }
                    };
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
    // Is set to the previous cache used by a state before updating the cache. If the new cache has shapes equivalent to
    // the ones in the old cache, we don't need to go through the map since the old one already had deduplicated shapes
    private static final ThreadLocal<BlockStateCacheAccess> LAST_CACHE = new ThreadLocal<>();

    // Calls before the cache for <code>state</code> is (re-)populated
    public static void deduplicateCachePre(AbstractBlockState state) {
        LAST_CACHE.set(GET_CACHE.getValue().apply(state));
    }

    // Calls after the cache for <code>state</code> is (re-)populated
    public static void deduplicateCachePost(AbstractBlockState state) {
        BlockStateCacheAccess newCache = GET_CACHE.getValue().apply(state);
        if (newCache != null) {
            final BlockStateCacheAccess oldCache = LAST_CACHE.get();
            deduplicateCollisionShape(newCache, oldCache);
            deduplicateRenderShapes(newCache, oldCache);
            LAST_CACHE.set(null);
        }
    }

    private static void deduplicateCollisionShape(
            BlockStateCacheAccess newCache, @Nullable BlockStateCacheAccess oldCache
    ) {
        VoxelShape dedupedCollisionShape;
        if (oldCache != null && VoxelShapeHash.INSTANCE.equals(
                oldCache.getCollisionShape(), newCache.getCollisionShape()
        )) {
            dedupedCollisionShape = oldCache.getCollisionShape();
        } else {
            dedupedCollisionShape = newCache.getCollisionShape();
            if (dedupedCollisionShape instanceof VoxelShapeArray) {
                dedupedCollisionShape = CACHE_COLLIDE.computeIfAbsent(
                        (VoxelShapeArray) dedupedCollisionShape, Function.identity()
                );
            }
        }
        replaceInternals(dedupedCollisionShape, newCache.getCollisionShape());
        newCache.setCollisionShape(dedupedCollisionShape);
    }

    private static void deduplicateRenderShapes(
            BlockStateCacheAccess newCache, @Nullable BlockStateCacheAccess oldCache
    ) {
        final VoxelShape newRenderShape = getRenderShape(newCache.getRenderShapes());
        if (newRenderShape == null) {
            return;
        }
        Pair<VoxelShape, VoxelShape[]> dedupedRenderShapes = null;
        if (oldCache != null) {
            final VoxelShape oldRenderShape = getRenderShape(oldCache.getRenderShapes());
            if (VoxelShapeHash.INSTANCE.equals(newRenderShape, oldRenderShape)) {
                dedupedRenderShapes = Pair.of(oldRenderShape, oldCache.getRenderShapes());
            }
        }
        if (dedupedRenderShapes == null) {
            // Who thought that this was a good interface for putIfAbsent…
            Pair<VoxelShape, VoxelShape[]> newPair = Pair.of(newRenderShape, newCache.getRenderShapes());
            dedupedRenderShapes = CACHE_PROJECT.putIfAbsent(newRenderShape, newPair);
            if (dedupedRenderShapes == null) {
                dedupedRenderShapes = newPair;
            }
        }
        replaceInternals(dedupedRenderShapes.getLeft(), newRenderShape);
        newCache.setRenderShapes(dedupedRenderShapes.getRight());
    }

    private static void replaceInternals(VoxelShape toKeep, VoxelShape toReplace) {
        if (toKeep instanceof VoxelShapeArray && toReplace instanceof VoxelShapeArray) {
            replaceInternals((VoxelShapeArray) toKeep, (VoxelShapeArray) toReplace);
        }
    }

    public static void replaceInternals(VoxelShapeArray toKeep, VoxelShapeArray toReplace) {
        if (toKeep == toReplace) {
            return;
        }
        // Mods have a tendency to keep their shapes in a custom cache, in addition to the blockstate cache. So removing
        // duplicate shapes from the cache only fixes part of the problem. The proper fix would be to deduplicate the
        // mod caches as well (or convince people to get rid of the larger ones), but that's not feasible. So: Accept
        // that we can't do anything about shallow size and replace the internals with those used in the cache. This is
        // not theoretically 100% safe since VSs can technically be modified after they are created, but handing out VSs
        // that will be modified is unsafe in any case since a lot of vanilla code relies on VSs being immutable.
        access(toReplace).setXPoints(access(toKeep).getXPoints());
        access(toReplace).setYPoints(access(toKeep).getYPoints());
        access(toReplace).setZPoints(access(toKeep).getZPoints());
        accessVS(toReplace).setProjectionCache(accessVS(toKeep).getProjectionCache());
        accessVS(toReplace).setPart(accessVS(toKeep).getPart());
    }

    @SuppressWarnings("ConstantConditions")
    private static VSArrayAccess access(VoxelShapeArray a) {
        return (VSArrayAccess) (Object) a;
    }

    private static VoxelShapeAccess accessVS(VoxelShape a) {
        return (VoxelShapeAccess) a;
    }

    @Nullable
    private static VoxelShape getRenderShape(@Nullable VoxelShape[] projected) {
        if (projected != null) {
            for (VoxelShape side : projected) {
                if (side instanceof SplitVoxelShape) {
                    return ((VSSplitAccess) side).getShape();
                }
            }
        }
        return null;
    }
}
