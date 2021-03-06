package malte0811.ferritecore.impl;

import com.google.common.base.Splitter;
import malte0811.ferritecore.util.PredicateHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PropertyValueConditionImpl {
    private static final Map<Pair<Property<?>, Comparable<?>>, Predicate<BlockState>> STATE_HAS_PROPERTY_CACHE = new ConcurrentHashMap<>();

    /**
     * A copy of {@link net.minecraft.client.renderer.model.multipart.PropertyValueCondition#getPredicate(StateContainer)}
     * since targeting the correct line is near impossible
     */
    public static Predicate<BlockState> getPredicate(
            StateContainer<Block, BlockState> stateContainer, String key, String value, Splitter splitter
    ) {
        Property<?> property = stateContainer.getProperty(key);
        if (property == null) {
            throw new RuntimeException(String.format(
                    "Unknown property '%s' on '%s'", key, stateContainer.getOwner().toString()
            ));
        } else {
            String valueNoInvert = value;
            boolean invert = !valueNoInvert.isEmpty() && valueNoInvert.charAt(0) == '!';
            if (invert) {
                valueNoInvert = valueNoInvert.substring(1);
            }

            List<String> matchedStates = splitter.splitToList(valueNoInvert);
            if (matchedStates.isEmpty()) {
                throw new RuntimeException(String.format(
                        "Empty value '%s' for property '%s' on '%s'",
                        value, key, stateContainer.getOwner().toString()
                ));
            } else {
                Predicate<BlockState> isMatchedState;
                if (matchedStates.size() == 1) {
                    isMatchedState = makePropertyPredicate(stateContainer, property, valueNoInvert, key, value);
                } else {
                    List<Predicate<BlockState>> subPredicates = matchedStates.stream()
                            .map(subValue -> makePropertyPredicate(stateContainer, property, subValue, key, value))
                            .collect(Collectors.toList());
                    // This line is the only functional change, but targeting it with anything but Overwrite appears to
                    // be impossible
                    PredicateHelper.canonize(subPredicates);
                    isMatchedState = Deduplicator.or(subPredicates);
                }

                return invert ? isMatchedState.negate() : isMatchedState;
            }
        }
    }

    private static <T extends Comparable<T>>
    Predicate<BlockState> makePropertyPredicate(
            StateContainer<Block, BlockState> container, Property<T> property, String subValue, String key, String value
    ) {
        Optional<T> optional = property.parseValue(subValue);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.format(
                    "Unknown value '%s' for property '%s' on '%s' in '%s'",
                    subValue, key, container.getOwner().toString(), value
            ));
        } else {
            T unwrapped = optional.get();
            return STATE_HAS_PROPERTY_CACHE.computeIfAbsent(
                    Pair.of(property, unwrapped),
                    pair -> {
                        Comparable<?> valueInt = pair.getRight();
                        Property<?> propInt = pair.getLeft();
                        return state -> state.get(propInt).equals(valueInt);
                    }
            );
        }
    }
}
