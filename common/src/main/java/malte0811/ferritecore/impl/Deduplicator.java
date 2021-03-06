package malte0811.ferritecore.impl;

import com.mojang.datafixers.util.Unit;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import malte0811.ferritecore.hash.LambdaBasedHash;
import malte0811.ferritecore.mixin.dedupbakedquad.BakedQuadAccess;
import malte0811.ferritecore.util.PredicateHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Deduplicator {
    private static final Map<String, String> VARIANT_IDENTITIES = new ConcurrentHashMap<>();
    // Typedefs would be a nice thing to have
    private static final Map<List<Pair<Predicate<BlockState>, IBakedModel>>, MultipartBakedModel> KNOWN_MULTIPART_MODELS = new ConcurrentHashMap<>();
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> OR_PREDICATE_CACHE = new ConcurrentHashMap<>();
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> AND_PREDICATE_CACHE = new ConcurrentHashMap<>();
    private static final Object2ObjectOpenCustomHashMap<int[], int[]> BAKED_QUAD_CACHE = new Object2ObjectOpenCustomHashMap<>(
            new LambdaBasedHash<>(Arrays::hashCode, Arrays::equals)
    );

    public static String deduplicateVariant(String variant) {
        return VARIANT_IDENTITIES.computeIfAbsent(variant, Function.identity());
    }

    public static MultipartBakedModel makeMultipartModel(List<Pair<Predicate<BlockState>, IBakedModel>> selectors) {
        return KNOWN_MULTIPART_MODELS.computeIfAbsent(selectors, MultipartBakedModel::new);
    }

    public static Predicate<BlockState> or(List<Predicate<BlockState>> list) {
        return OR_PREDICATE_CACHE.computeIfAbsent(list, PredicateHelper::or);
    }

    public static Predicate<BlockState> and(List<Predicate<BlockState>> list) {
        return AND_PREDICATE_CACHE.computeIfAbsent(list, PredicateHelper::and);
    }

    public static void deduplicate(BakedQuad bq) {
        synchronized (BAKED_QUAD_CACHE) {
            int[] deduped = BAKED_QUAD_CACHE.computeIfAbsent(bq.getVertexData(), Function.identity());
            ((BakedQuadAccess) bq).setVertexData(deduped);
        }
    }

    public static void registerReloadListener() {
        // Register the reload listener s.t. its "sync" part runs after the model loader reload
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ReloadListener<Unit>() {
            @Override
            protected Unit prepare(IResourceManager iResourceManager, IProfiler iProfiler) {
                return Unit.INSTANCE;
            }

            @Override
            protected void apply(Unit object, IResourceManager iResourceManager, IProfiler iProfiler) {
                VARIANT_IDENTITIES.clear();
                KNOWN_MULTIPART_MODELS.clear();
                OR_PREDICATE_CACHE.clear();
                AND_PREDICATE_CACHE.clear();
                BAKED_QUAD_CACHE.clear();
                BAKED_QUAD_CACHE.trim();
            }
        });
    }
}
