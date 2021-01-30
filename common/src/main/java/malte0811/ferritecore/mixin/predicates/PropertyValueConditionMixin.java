package malte0811.ferritecore.mixin.predicates;

import com.google.common.base.Splitter;
import malte0811.ferritecore.impl.PropertyValueConditionImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.PropertyValueCondition;
import net.minecraft.state.StateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(PropertyValueCondition.class)
public class PropertyValueConditionMixin {
    @Shadow
    @Final
    private String key;
    @Shadow
    @Final
    private String value;
    @Shadow
    @Final
    private static Splitter SPLITTER;

    /**
     * @reason Use cached predicates in the case of multiple specified values
     * A less invasive Mixin would be preferable (especially since only one line really changes), but that would involve
     * redirecting a lambda creation (not currently possible as far as I can tell) and capturing locals (possible, but
     * annoying)
     * @author malte0811
     */
    @Overwrite
    public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> stateContainer) {
        return PropertyValueConditionImpl.getPredicate(stateContainer, key, value, SPLITTER);
    }
}
