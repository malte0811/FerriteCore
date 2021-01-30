package malte0811.ferritecore.impl;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import malte0811.ferritecore.hash.VoxelShapeArrayHash;
import malte0811.ferritecore.hash.VoxelShapeHash;
import malte0811.ferritecore.mixin.blockstatecache.VSArrayAccess;
import malte0811.ferritecore.mixin.blockstatecache.VoxelShapeAccess;
import malte0811.ferritecore.util.LastAccessedCache;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeArray;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

public class BlockStateCacheImpl {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final Object2ObjectOpenCustomHashMap<VoxelShapeArray, VoxelShapeArray> CACHE_COLLIDE =
            new Object2ObjectOpenCustomHashMap<>(VoxelShapeArrayHash.INSTANCE);
    public static final LastAccessedCache<VoxelShape, VoxelShape[]> CACHE_PROJECT = new LastAccessedCache<>(
            VoxelShapeHash.INSTANCE, vs -> {
        VoxelShape[] result = new VoxelShape[DIRECTIONS.length];
        for (Direction d : DIRECTIONS) {
            result[d.ordinal()] = VoxelShapes.getFaceShape(vs, d);
        }
        return result;
    }
    );
    public static int collideCalls = 0;
    public static int projectCalls = 0;

    public static void resetCaches() {
        //TODO remove
        Logger logger = LogManager.getLogger();
        logger.info("Collide stats: Cache size: {}, calls: {}", CACHE_COLLIDE.size(), collideCalls);
        logger.info("Project stats: Cache size: {}, calls: {}", CACHE_PROJECT.size(), projectCalls);

        CACHE_COLLIDE.clear();
        CACHE_COLLIDE.trim();
        collideCalls = 0;
        CACHE_PROJECT.clear();
        projectCalls = 0;
    }

    public static VoxelShape redirectGetCollisionShape(
            AbstractBlock block, BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context
    ) {
        VoxelShape baseResult = block.getCollisionShape(state, worldIn, pos, context);
        if (!(baseResult instanceof VoxelShapeArray)) {
            return baseResult;
        }
        VoxelShapeArray baseArray = (VoxelShapeArray) baseResult;
        ++collideCalls;
        VoxelShapeArray resultArray = CACHE_COLLIDE.computeIfAbsent(baseArray, Function.identity());
        replaceInternals(resultArray, baseArray);
        return resultArray;
    }

    public static VoxelShape redirectFaceShape(VoxelShape shape, Direction face) {
        ++projectCalls;
        Pair<VoxelShape, VoxelShape[]> sides = CACHE_PROJECT.get(shape);
        if (sides.getFirst() instanceof VoxelShapeArray && shape instanceof VoxelShapeArray) {
            replaceInternals((VoxelShapeArray) sides.getFirst(), (VoxelShapeArray) shape);
        }
        return sides.getSecond()[face.ordinal()];
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
}
