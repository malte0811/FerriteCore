package malte0811.ferritecore;

import cpw.mods.modlauncher.api.INameMappingService;
import malte0811.ferritecore.util.Constants;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod(Constants.MODID)
public class ModMainForge {

    public ModMainForge() {
        if (!hasMixins()) {
            ModLoader.get().addWarning(new ModLoadingWarning(
                    ModLoadingContext.get().getActiveContainer().getModInfo(),
                    ModLoadingStage.CONSTRUCT,
                    "FerriteCore: Mixins are not available! Please install MixinBootstrap!"
            ));
            throw new RuntimeException("Mixins are not available! Please install MixinBootstrap!");
        }
        Constants.blockstateCacheFieldName = ObfuscationReflectionHelper.remapName(
                INameMappingService.Domain.FIELD, "f_60593_"
        );
        ModLoadingContext.get().registerExtensionPoint(
                DisplayTest.class, () -> new DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (s, b) -> true)
        );
    }

    private static boolean hasMixins() {
        // Replaced by SelfMixin if Mixins are provided by Forge itself, MixinBootstrap or something else
        return false;
    }
}
