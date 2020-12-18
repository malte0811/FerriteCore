package malte0811.ferritecore.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.PropertyValueCondition;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(PropertyValueCondition.class)
public class MixinPropertyValueCondition {
    @Shadow
    @Final
    private String key;
    @Shadow
    @Final
    private String value;

    /**
     * @reason The vanilla implementation captures an Optional in the resulting lambda, which eats a lot of memory. A
     * less aggressive Mixin would require fiddling with local variables, which is more effort than it would be worth
     * for such an obscure method.
     * @author malte0811
     */
    @Overwrite
    private <T extends Comparable<T>>
    Predicate<BlockState> makePropertyPredicate(
            StateContainer<Block, BlockState> container, Property<T> property, String value
    ) {
        Optional<T> optional = property.parseValue(value);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", value, this.key, container.getOwner().toString(), this.value));
        } else {
            // These are the only relevant lines. In vanilla optional.get is called inside the lambda
            T unwrapped = optional.get();
            return (state) -> state.get(property).equals(unwrapped);
        }
    }
}
