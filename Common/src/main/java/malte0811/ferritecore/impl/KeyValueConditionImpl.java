package malte0811.ferritecore.impl;

import com.google.common.base.Splitter;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import malte0811.ferritecore.util.PredicateHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeyValueConditionImpl {
    private static final Map<Pair<Property<?>, Comparable<?>>, Predicate<BlockState>> STATE_HAS_PROPERTY_CACHE =
            Collections.synchronizedMap(new Object2ObjectOpenCustomHashMap<>(new HashStrategy()));

    /**
     * A copy of {@link net.minecraft.client.renderer.block.model.multipart.KeyValueCondition#getPredicate(StateDefinition)}
     * since targeting the correct line is near impossible
     */
    public static Predicate<BlockState> getPredicate(
            StateDefinition<Block, BlockState> stateContainer, String key, String value, Splitter splitter
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
                    isMatchedState = getBlockStatePredicate(stateContainer, property, valueNoInvert, key, value);
                } else {
                    List<Predicate<BlockState>> subPredicates = matchedStates.stream()
                            .map(subValue -> getBlockStatePredicate(stateContainer, property, subValue, key, value))
                            .collect(Collectors.toCollection(ArrayList::new));
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
    Predicate<BlockState> getBlockStatePredicate(
            StateDefinition<Block, BlockState> container,
            Property<T> property,
            String subValue,
            String key,
            String value
    ) {
        Optional<T> optional = property.getValue(subValue);
        if (optional.isEmpty()) {
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
                        return state -> state.getValue(propInt).equals(valueInt);
                    }
            );
        }
    }

    /**
     * This needs to match the vanilla behavior, i.e. properties are always compared as references (see StateHolder)
     * while the values are "properly" compared.
     */
    private static class HashStrategy implements Hash.Strategy<Pair<Property<?>, Comparable<?>>> {
        @Override
        public int hashCode(Pair<Property<?>, Comparable<?>> pair) {
            if (pair == null) {
                return 0;
            } else {
                return 31 * System.identityHashCode(pair.getLeft()) + Objects.hashCode(pair.getRight());
            }
        }

        @Override
        public boolean equals(Pair<Property<?>, Comparable<?>> p1, Pair<Property<?>, Comparable<?>> p2) {
            if (p1 == null || p2 == null) {
                return p1 == p2;
            } else {
                return p1.getLeft() == p2.getLeft() && Objects.equals(p1.getRight(), p2.getRight());
            }
        }
    }
}
