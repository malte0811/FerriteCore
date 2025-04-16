package malte0811.ferritecore.mixin.blockmodellists;

import com.mojang.datafixers.util.Either;
import malte0811.ferritecore.util.CollectionUtil;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Map;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
    @ModifyArg(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel;<init>(Lnet/minecraft/resources/ResourceLocation;Ljava/util/List;Ljava/util/Map;Ljava/lang/Boolean;Lnet/minecraft/client/renderer/block/model/BlockModel$GuiLight;Lnet/minecraft/client/renderer/block/model/ItemTransforms;Ljava/util/List;)V"))
    private List<BlockElement> elementsImmutableCopy(List<BlockElement> list) {
        return CollectionUtil.minimize(list);
    }

    @ModifyArg(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel;<init>(Lnet/minecraft/resources/ResourceLocation;Ljava/util/List;Ljava/util/Map;Ljava/lang/Boolean;Lnet/minecraft/client/renderer/block/model/BlockModel$GuiLight;Lnet/minecraft/client/renderer/block/model/ItemTransforms;Ljava/util/List;)V"))
    private Map<String, Either<Material, String>> immutableCopy(Map<String, Either<Material, String>> map) {
        return Map.copyOf(map);
    }

    @ModifyArg(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", index = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/BlockModel;<init>(Lnet/minecraft/resources/ResourceLocation;Ljava/util/List;Ljava/util/Map;Ljava/lang/Boolean;Lnet/minecraft/client/renderer/block/model/BlockModel$GuiLight;Lnet/minecraft/client/renderer/block/model/ItemTransforms;Ljava/util/List;)V"))
    private List<ItemOverride> overridesImmutableCopy(List<ItemOverride> list) {
        return CollectionUtil.minimize(list);
    }
}
