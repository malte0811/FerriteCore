package malte0811.ferritecore.mixin.predicates;

import com.google.common.base.Splitter;
import malte0811.ferritecore.impl.KeyValueConditionImpl;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(value = KeyValueCondition.class, priority = 2000)
public class KeyValueConditionMixin {
    @Shadow
    @Final
    private String key;
    @Shadow
    @Final
    private String value;
    @Shadow
    @Final
    private static Splitter PIPE_SPLITTER;

    /**
     * @reason Use cached predicates in the case of multiple specified values
     * A less invasive Mixin would be preferable (especially since only one line really changes), but that would involve
     * redirecting a lambda creation (not currently possible as far as I can tell) and capturing locals (possible, but
     * annoying)
     * @author malte0811
     */
    @Overwrite
    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> stateContainer) {
        return KeyValueConditionImpl.getPredicate(stateContainer, key, value, PIPE_SPLITTER);
    }
}
