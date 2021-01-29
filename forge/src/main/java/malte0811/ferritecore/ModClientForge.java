package malte0811.ferritecore;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientForge {
    public static void registerReloadListener(ParticleFactoryRegisterEvent ev) {
        Deduplicator.registerReloadListener();
    }
}
