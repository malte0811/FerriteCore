package malte0811.ferritecore;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformHooks implements IPlatformHooks {
    @Override
    public String computeBlockstateCacheFieldName() {
        return FabricLoader.getInstance()
                .getMappingResolver()
                .mapFieldName(
                        "official",
                        "net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase",
                        // cache
                        "cache",
                        // AbstractBlockState.Cache
                        "Lnet.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase$Cache;"
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
