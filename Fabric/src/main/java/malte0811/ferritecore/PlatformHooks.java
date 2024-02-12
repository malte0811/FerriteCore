package malte0811.ferritecore;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformHooks implements IPlatformHooks {
    @Override
    public String computeBlockstateCacheFieldName() {
        return FabricLoader.getInstance()
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

    @Override
    public String computeStateHolderValuesName() {
        return FabricLoader.getInstance()
                .getMappingResolver()
                .mapFieldName(
                        "intermediary",
                        // StateHolder
                        "net.minecraft.class_2688",
                        // values
                        "field_24738",
                        "Lit/unimi/dsi/fastutil/objects/Reference2ObjectArrayMap;"
                );
    }
}
