package malte0811.ferritecore;

import malte0811.ferritecore.util.Constants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ModMain implements ModInitializer {
    @Override
    public void onInitialize() {
        Constants.blockstateCacheFieldName = FabricLoader.getInstance()
                .getMappingResolver()
                .mapFieldName(
                        "intermediary",
                        // AbstractBlockState
                        "net.minecraft.class_4970$class_4971",
                        // cache
                        "field_23166",
                        // AbstractBlockState.Cache
                        "Lnet/minecraft/class_4970$class_4971$class_3752;"
                );
    }
}
