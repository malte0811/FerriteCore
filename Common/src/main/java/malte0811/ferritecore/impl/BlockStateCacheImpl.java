package malte0811.ferritecore.impl;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import malte0811.ferritecore.ducks.BlockStateCacheAccess;
import malte0811.ferritecore.hash.ArrayVoxelShapeHash;
import malte0811.ferritecore.hash.VoxelShapeHash;
import malte0811.ferritecore.mixin.accessors.ArrayVSAccess;
import malte0811.ferritecore.mixin.accessors.SliceShapeAccess;
import malte0811.ferritecore.util.Constants;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockStateCacheImpl {
    public static final Map<ArrayVSAccess, ArrayVSAccess> CACHE_COLLIDE = new Object2ObjectOpenCustomHashMap<>(
            ArrayVoxelShapeHash.INSTANCE
    );
    // Maps a shape to the "canonical instance" of that shape and its side projections
    public static final Map<VoxelShape, Pair<VoxelShape, VoxelShape[]>> CACHE_PROJECT =
            new Object2ObjectOpenCustomHashMap<>(VoxelShapeHash.INSTANCE);
    public static final Map<boolean[], boolean[]> CACHE_FACE_STURDY = new Object2ObjectOpenCustomHashMap<>(
            BooleanArrays.HASH_STRATEGY
    );

    // Get the cache from a blockstate. Mixin does not handle private inner classes too well, so method handles and
    // manual remapping it is
    private static final Supplier<Function<BlockStateBase, BlockStateCacheAccess>> GET_CACHE = Suppliers.memoize(() -> {
        try {
            final String cacheName = Constants.PLATFORM_HOOKS.computeBlockstateCacheFieldName();
            final Field cacheField = BlockStateBase.class.getDeclaredField(cacheName);
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
    public static void deduplicateCachePre(BlockStateBase state) {
        LAST_CACHE.set(GET_CACHE.get().apply(state));
    }

    // Calls after the cache for <code>state</code> is (re-)populated
    public static void deduplicateCachePost(BlockStateBase state) {
        BlockStateCacheAccess newCache = GET_CACHE.get().apply(state);
        if (newCache != null) {
            final BlockStateCacheAccess oldCache = LAST_CACHE.get();
            deduplicateCollisionShape(newCache, oldCache);
            deduplicateRenderShapes(newCache, oldCache);
            deduplicateFaceSturdyArray(newCache, oldCache);
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
            if (dedupedCollisionShape instanceof ArrayVSAccess access) {
                dedupedCollisionShape = (VoxelShape) CACHE_COLLIDE.computeIfAbsent(access, Function.identity());
            }
        }
        replaceInternals(dedupedCollisionShape, newCache.getCollisionShape());
        newCache.setCollisionShape(dedupedCollisionShape);
    }

    private static void deduplicateRenderShapes(
            BlockStateCacheAccess newCache, @Nullable BlockStateCacheAccess oldCache
    ) {
        final VoxelShape newRenderShape = getRenderShape(newCache.getOcclusionShapes());
        if (newRenderShape == null) {
            return;
        }
        Pair<VoxelShape, VoxelShape[]> dedupedRenderShapes = null;
        if (oldCache != null) {
            final VoxelShape oldRenderShape = getRenderShape(oldCache.getOcclusionShapes());
            if (VoxelShapeHash.INSTANCE.equals(newRenderShape, oldRenderShape)) {
                dedupedRenderShapes = Pair.of(oldRenderShape, oldCache.getOcclusionShapes());
            }
        }
        if (dedupedRenderShapes == null) {
            // Who thought that this was a good interface for putIfAbsent…
            Pair<VoxelShape, VoxelShape[]> newPair = Pair.of(newRenderShape, newCache.getOcclusionShapes());
            dedupedRenderShapes = CACHE_PROJECT.putIfAbsent(newRenderShape, newPair);
            if (dedupedRenderShapes == null) {
                dedupedRenderShapes = newPair;
            }
        }
        replaceInternals(dedupedRenderShapes.getLeft(), newRenderShape);
        newCache.setOcclusionShapes(dedupedRenderShapes.getRight());
    }

    private static void deduplicateFaceSturdyArray(
            BlockStateCacheAccess newCache, @Nullable BlockStateCacheAccess oldCache
    ) {
        boolean[] dedupedFaceSturdy;
        if(oldCache != null && Arrays.equals(oldCache.getFaceSturdy(), newCache.getFaceSturdy())) {
            dedupedFaceSturdy = oldCache.getFaceSturdy();
        } else {
            dedupedFaceSturdy = CACHE_FACE_STURDY.computeIfAbsent(newCache.getFaceSturdy(), Function.identity());
        }
        newCache.setFaceSturdy(dedupedFaceSturdy);
    }

    private static void replaceInternals(VoxelShape toKeep, VoxelShape toReplace) {
        if (toKeep instanceof ArrayVoxelShape keepArray && toReplace instanceof ArrayVoxelShape replaceArray) {
            replaceInternals(keepArray, replaceArray);
        }
    }

    public static void replaceInternals(ArrayVoxelShape toKeep, ArrayVoxelShape toReplace) {
        if (toKeep == toReplace) {
            return;
        }
        // Mods have a tendency to keep their shapes in a custom cache, in addition to the blockstate cache. So removing
        // duplicate shapes from the cache only fixes part of the problem. The proper fix would be to deduplicate the
        // mod caches as well (or convince people to get rid of the larger ones), but that's not feasible. So: Accept
        // that we can't do anything about shallow size and replace the internals with those used in the cache. This is
        // not theoretically 100% safe since VSs can technically be modified after they are created, but handing out VSs
        // that will be modified is unsafe in any case since a lot of vanilla code relies on VSs being immutable.
        ArrayVSAccess toReplaceAccess = (ArrayVSAccess) toReplace;
        ArrayVSAccess toKeepAccess = (ArrayVSAccess) toKeep;
        toReplaceAccess.setXPoints(toKeepAccess.getXPoints());
        toReplaceAccess.setYPoints(toKeepAccess.getYPoints());
        toReplaceAccess.setZPoints(toKeepAccess.getZPoints());
        toReplaceAccess.setFaces(toKeepAccess.getFaces());
        toReplaceAccess.setShape(toKeepAccess.getShape());
    }

    @Nullable
    private static VoxelShape getRenderShape(@Nullable VoxelShape[] projected) {
        if (projected != null) {
            for (VoxelShape side : projected) {
                if (side instanceof SliceShapeAccess slice) {
                    return slice.getDelegate();
                }
            }
        }
        return null;
    }
}
