package malte0811.ferritecore.mixin.dedupmultipart;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.BitSet;
import java.util.Map;

/**
 * The map implementation used for {@link MultiPartBakedModel#selectorCache} ({@link Object2ObjectOpenCustomHashMap})
 * is not thread-safe, but getQuads is called in parallel in vanilla (and even more so in Forge with
 * alwaysSetupTerrainOffThread=true). The only reason this works for vanilla is that the cache will never contain more
 * than a single blockstate, since a new instance is created for each blockstate (this is probably unintentional, a map
 * would be a weird choice for this scenario). {@link MixinMultipartBuilder} re-uses the equivalent models, so the cache
 * can grow beyond a single element (as is probably intended). If a put-call causes the backing array to be resized
 * concurrent get-calls can (and will) crash, so we need to synchronize them.<br>
 * It is not clear if this implementation (naive synchronization on the cache) is optimal w.r.t.
 * runtime/parallelization, in my experience this part of the code is not runtime-critical enough to put significant
 * effort into fancy parallelization solutions (may change in the future).
 */
// Non-final fields: Work around Java/Mixin limitations
// Unresolved reference: Forge adds a parameter to getQuads, so the usual remapping process breaks and I need to specify
// SRG and intermediary names directly, which confuses the MCDev IntelliJ plugin
@SuppressWarnings({"SynchronizeOnNonFinalField", "UnresolvedMixinReference"})
@Mixin(MultiPartBakedModel.class)
public class MixinMultipartModel {
    @Shadow
    @Final
    private Map<BlockState, BitSet> selectorCache;

    @Redirect(
            method = {"method_4707", "func_200117_a", "getQuads"},
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false
    )
    public <K, V> V redirectCacheGet(Map<K, V> map, K key) {
        synchronized (selectorCache) {
            return map.get(key);
        }
    }

    @Redirect(
            method = {"method_4707", "func_200117_a", "getQuads"},
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false
    )
    public <K, V> V redirectCachePut(Map<K, V> map, K key, V value) {
        synchronized (selectorCache) {
            return map.put(key, value);
        }
    }
}
