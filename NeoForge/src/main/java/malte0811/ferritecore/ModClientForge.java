package malte0811.ferritecore;

import malte0811.ferritecore.impl.Deduplicator;
import malte0811.ferritecore.util.Constants;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import org.apache.http.impl.client.RedirectLocations;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MODID)
public class ModClientForge {
    @SubscribeEvent
    public static void registerReloadListener(AddClientReloadListenersEvent ev) {
        ev.addListener(
                ResourceLocation.fromNamespaceAndPath(Constants.MODID, "deduplicator"),
                Deduplicator.createReloadListener()
        );
    }
}
