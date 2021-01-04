package malte0811.ferritecore.mixin.dedupmultipart;

import malte0811.ferritecore.impl.Deduplicator;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.Builder.class)
public class MixinMultipartBuilder {
    @Shadow
    @Final
    private List<Pair<Predicate<BlockState>, IBakedModel>> selectors;

    /**
     * @reason Cache/deduplicate returned model
     * @author malte0811
     */
    @Overwrite
    public IBakedModel build() {
        return Deduplicator.makeMultipartModel(selectors);
    }
}
