package malte0811.ferritecore;

import cpw.mods.modlauncher.api.INameMappingService;
import malte0811.ferritecore.util.Constants;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod(Constants.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModMainForge {

    public ModMainForge() {
        Constants.blockstateCacheFieldName = ObfuscationReflectionHelper.remapName(
                INameMappingService.Domain.FIELD, "field_215707_c"
        );
        ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (s, b) -> true)
        );
    }
}
