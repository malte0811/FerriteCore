package malte0811.ferritecore.mixin.dedupmultipart;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.Builder.class)
public class MixinMultipartBuilder {
    @Redirect(
            method = "build",
            at = @At(value = "NEW", target = "net/minecraft/client/renderer/model/MultipartBakedModel")
    )
    public MultipartBakedModel build(List<Pair<Predicate<BlockState>, IBakedModel>> selectors) {
        return Deduplicator.makeMultipartModel(selectors);
    }
}
