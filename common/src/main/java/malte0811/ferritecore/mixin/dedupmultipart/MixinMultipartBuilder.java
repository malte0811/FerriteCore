package malte0811.ferritecore.mixin.dedupmultipart;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(MultiPartBakedModel.Builder.class)
public class MixinMultipartBuilder {
    @Redirect(
            method = "build",
            at = @At(value = "NEW", target = "net/minecraft/client/resources/model/MultiPartBakedModel")
    )
    public MultiPartBakedModel build(List<Pair<Predicate<BlockState>, BakedModel>> selectors) {
        return Deduplicator.makeMultipartModel(selectors);
    }
}
