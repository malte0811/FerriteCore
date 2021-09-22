package malte0811.ferritecore;

import cpw.mods.modlauncher.api.INameMappingService;
import malte0811.ferritecore.util.Constants;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod(Constants.MODID)
public class ModMainForge {

    public ModMainForge() {
        Constants.blockstateCacheFieldName = ObfuscationReflectionHelper.remapName(
                INameMappingService.Domain.FIELD, "f_60593_"
        );
        ModLoadingContext.get().registerExtensionPoint(
                DisplayTest.class, () -> new DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (s, b) -> true)
        );
    }
}
