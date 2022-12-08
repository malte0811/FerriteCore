package malte0811.ferritecore;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class PlatformHooks implements IPlatformHooks {
    @Override
    public String computeBlockstateCacheFieldName() {
        return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_60593_");
    }
}
