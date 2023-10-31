package malte0811.ferritecore;

import malte0811.ferritecore.util.Constants;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.NetworkConstants;

@Mod(Constants.MODID)
public class ModMainForge {

    public ModMainForge() {
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (s, b) -> true)
        );
    }
}
