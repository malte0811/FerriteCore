package malte0811.ferritecore.mixin;

import com.google.common.base.Splitter;
import malte0811.ferritecore.CachedOrPredicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.PropertyValueCondition;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(PropertyValueCondition.class)
public class PropertyValueConditionMixin {
    private static final Map<Pair<Property<?>, Comparable<?>>, Predicate<BlockState>> STATE_HAS_PROPERTY_CACHE = new ConcurrentHashMap<>();

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
        Property<?> property = stateContainer.getProperty(this.key);
        if (property == null) {
            throw new RuntimeException(String.format(
                    "Unknown property '%s' on '%s'", this.key, stateContainer.getOwner().toString()
            ));
        } else {
            String valueNoInvert = this.value;
            boolean invert = !valueNoInvert.isEmpty() && valueNoInvert.charAt(0) == '!';
            if (invert) {
                valueNoInvert = valueNoInvert.substring(1);
            }

            List<String> matchedStates = SPLITTER.splitToList(valueNoInvert);
            if (matchedStates.isEmpty()) {
                throw new RuntimeException(String.format(
                        "Empty value '%s' for property '%s' on '%s'",
                        this.value, this.key, stateContainer.getOwner().toString()
                ));
            } else {
                Predicate<BlockState> isMatchedState;
                if (matchedStates.size() == 1) {
                    isMatchedState = this.makePropertyPredicate(stateContainer, property, valueNoInvert);
                } else {
                    List<Predicate<BlockState>> subPredicates = matchedStates.stream()
                            .map(subValue -> this.makePropertyPredicate(stateContainer, property, subValue))
                            .collect(Collectors.toList());
                    // This line is the only functional change, but targeting it with anything but Overwrite appears to
                    // be impossible
                    isMatchedState = CachedOrPredicates.or(subPredicates);
                }

                return invert ? isMatchedState.negate() : isMatchedState;
            }
        }
    }

    /**
     * @reason The vanilla implementation captures an Optional in the resulting lambda, which eats a lot of memory. A
     * less aggressive Mixin would require fiddling with local variables, which is more effort than it would be worth
     * for such an obscure method.
     * Also adds a cache to avoid creating two Predicate instances for the same lookup
     * @author malte0811
     */
    @Overwrite
    private <T extends Comparable<T>>
    Predicate<BlockState> makePropertyPredicate(
            StateContainer<Block, BlockState> container, Property<T> property, String value
    ) {
        Optional<T> optional = property.parseValue(value);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.format(
                    "Unknown value '%s' for property '%s' on '%s' in '%s'",
                    value, this.key, container.getOwner().toString(), this.value
            ));
        } else {
            T unwrapped = optional.get();
            Pair<Property<?>, Comparable<?>> key = Pair.of(property, unwrapped);
            return STATE_HAS_PROPERTY_CACHE.computeIfAbsent(
                    key,
                    pair -> {
                        Comparable<?> valueInt = pair.getRight();
                        Property<?> propInt = pair.getLeft();
                        return state -> state.get(propInt).equals(valueInt);
                    }
            );
        }
    }
}
