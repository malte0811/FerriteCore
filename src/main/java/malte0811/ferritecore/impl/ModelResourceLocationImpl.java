package malte0811.ferritecore.impl;

import com.mojang.datafixers.util.Unit;
import malte0811.ferritecore.ModMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModelResourceLocationImpl {
    private static final Map<String, String> VARIANT_IDENTITIES = new ConcurrentHashMap<>();

    public static String deduplicateVariant(String variant) {
        return VARIANT_IDENTITIES.computeIfAbsent(variant, Function.identity());
    }

    @SubscribeEvent
    public static void registerReloadListener(ParticleFactoryRegisterEvent ev) {
        // Register the reload listener s.t. its "sync" part runs after the model loader reload
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new ReloadListener<Unit>() {
            @Nonnull
            @Override
            protected Unit prepare(
                    @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn
            ) {
                return Unit.INSTANCE;
            }

            @Override
            protected void apply(
                    @Nonnull Unit objectIn, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn
            ) {
                VARIANT_IDENTITIES.clear();
            }
        });
    }
}
