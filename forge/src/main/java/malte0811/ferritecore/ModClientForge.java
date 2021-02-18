package malte0811.ferritecore;

import malte0811.ferritecore.impl.Deduplicator;
import malte0811.ferritecore.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientForge {
    @SubscribeEvent
    public static void registerReloadListener(ParticleFactoryRegisterEvent ev) {
        Deduplicator.registerReloadListener();
    }
}
