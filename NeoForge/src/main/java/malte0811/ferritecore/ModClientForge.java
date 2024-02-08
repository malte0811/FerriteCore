package malte0811.ferritecore;

import malte0811.ferritecore.impl.Deduplicator;
import malte0811.ferritecore.util.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModClientForge {
    @SubscribeEvent
    public static void registerReloadListener(RenderLevelStageEvent.RegisterStageEvent ev) {
        Deduplicator.registerReloadListener();
    }
}
