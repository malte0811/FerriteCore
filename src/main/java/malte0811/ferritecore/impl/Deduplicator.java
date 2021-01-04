package malte0811.ferritecore.impl;

import com.mojang.datafixers.util.Unit;
import malte0811.ferritecore.ModMain;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Deduplicator {
    private static final Map<String, String> VARIANT_IDENTITIES = new ConcurrentHashMap<>();
    // Typedefs would be a nice thing to have
    private static final Map<List<Pair<Predicate<BlockState>, IBakedModel>>, MultipartBakedModel> KNOWN_MULTIPART_MODELS = new ConcurrentHashMap<>();
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> OR_PREDICATE_CACHE = new ConcurrentHashMap<>();
    private static final Map<List<Predicate<BlockState>>, Predicate<BlockState>> AND_PREDICATE_CACHE = new ConcurrentHashMap<>();

    public static String deduplicateVariant(String variant) {
        return VARIANT_IDENTITIES.computeIfAbsent(variant, Function.identity());
    }

    public static MultipartBakedModel makeMultipartModel(List<Pair<Predicate<BlockState>, IBakedModel>> selectors) {
        return KNOWN_MULTIPART_MODELS.computeIfAbsent(selectors, MultipartBakedModel::new);
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

    @SubscribeEvent
    public static void registerReloadListener(ParticleFactoryRegisterEvent ev) {
        // Register the reload listener s.t. its "sync" part runs after the model loader reload
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ReloadListener<Unit>() {
            @Nonnull
            @Override
            protected Unit prepare(@Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {
                return Unit.INSTANCE;
            }

            @Override
            protected void apply(
                    @Nonnull Unit objectIn, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn
            ) {
                VARIANT_IDENTITIES.clear();
                KNOWN_MULTIPART_MODELS.clear();
                OR_PREDICATE_CACHE.clear();
                AND_PREDICATE_CACHE.clear();
            }
        });
    }
}
