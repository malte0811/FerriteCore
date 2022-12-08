package malte0811.ferritecore;

import cpw.mods.modlauncher.api.INameMappingService;
import malte0811.ferritecore.util.Constants;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkConstants;

@Mod(Constants.MODID)
public class ModMainForge {

    public ModMainForge() {
        ModLoadingContext.get().registerExtensionPoint(
                DisplayTest.class, () -> new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (s, b) -> true)
        );
    }
}
