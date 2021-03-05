package malte0811.ferritecore.impl;

import net.minecraft.state.Property;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PropertyValueConditionImpl {
    public static final PredicateCache<Pair<Property<?>, String>> SINGLE_VALUE_CACHE = new PredicateCache<>();
    public static final PredicateCache<PredicateKey> FULL_PREDICATE_CACHE = new PredicateCache<>();

    public static class PredicateKey {
        private final Property<?> prop;
        private final List<String> allowed;
        private final boolean inverted;

        public PredicateKey(Property<?> prop, List<String> allowed, boolean inverted) {
            this.prop = prop;
            this.allowed = new ArrayList<>(allowed);
            this.allowed.sort(Comparator.naturalOrder());
            this.inverted = inverted;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PredicateKey that = (PredicateKey) o;
            return inverted == that.inverted && prop.equals(that.prop) && allowed.equals(that.allowed);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prop, allowed, inverted);
        }
    }
}
