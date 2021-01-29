package malte0811.ferritecore;

import malte0811.ferritecore.impl.BlockStateCacheImpl;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;

@Mod(ModMain.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModMainForge {
    // Caches are populated in two places: a) In ITagCollectionSupplier#updateTags (which triggers this event)
    @SubscribeEvent
    public static void onTagReloadVanilla(TagsUpdatedEvent.VanillaTagTypes ignored) {
        BlockStateCacheImpl.resetCaches();
    }

    // b) Via ForgeRegistry#bake, which usually triggers this event
    @SubscribeEvent
    public static void onModIdMapping(FMLModIdMappingEvent ignored) {
        BlockStateCacheImpl.resetCaches();
    }
}
