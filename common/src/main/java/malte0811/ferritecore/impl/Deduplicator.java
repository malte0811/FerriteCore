package malte0811.ferritecore.impl;

import com.mojang.datafixers.util.Unit;
import malte0811.ferritecore.ModMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

//TODO @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Deduplicator {
    private static final Map<String, String> VARIANT_IDENTITIES = new ConcurrentHashMap<>();
    // Typedefs would be a nice thing to have
    private static final Map<List<Pair<Predicate<BlockState>, BakedModel>>, MultiPartBakedModel> KNOWN_MULTIPART_MODELS = new ConcurrentHashMap<>();
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> OR_PREDICATE_CACHE = new ConcurrentHashMap<>();
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> AND_PREDICATE_CACHE = new ConcurrentHashMap<>();

    public static String deduplicateVariant(String variant) {
        return VARIANT_IDENTITIES.computeIfAbsent(variant, Function.identity());
    }

    public static MultiPartBakedModel makeMultipartModel(List<Pair<Predicate<BlockState>, BakedModel>> selectors) {
        return KNOWN_MULTIPART_MODELS.computeIfAbsent(selectors, MultiPartBakedModel::new);
    }

    public static Predicate<BlockState> or(List<Predicate<BlockState>> list) {
        return OR_PREDICATE_CACHE.computeIfAbsent(
                list,
                listInt -> state -> listInt.stream().anyMatch((predicate) -> predicate.test(state))
        );
    }

    public static Predicate<BlockState> and(List<Predicate<BlockState>> list) {
        return AND_PREDICATE_CACHE.computeIfAbsent(
                list,
                listInt -> state -> listInt.stream().allMatch((predicate) -> predicate.test(state))
        );
    }

    //TODO @SubscribeEvent
    public static void registerReloadListener() {
        // Register the reload listener s.t. its "sync" part runs after the model loader reload
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(new SimplePreparableReloadListener<Unit>() {
            @Override
            protected Unit prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
                return Unit.INSTANCE;
            }

            @Override
            protected void apply(
                    Unit object, ResourceManager resourceManager, ProfilerFiller profilerFiller
            ) {
                VARIANT_IDENTITIES.clear();
                KNOWN_MULTIPART_MODELS.clear();
                OR_PREDICATE_CACHE.clear();
                AND_PREDICATE_CACHE.clear();
            }
        });
    }
}
