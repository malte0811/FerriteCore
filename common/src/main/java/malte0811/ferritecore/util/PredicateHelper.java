package malte0811.ferritecore.util;

import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class PredicateHelper {
    public static List<Predicate<BlockState>> toCanonicalList(
            Iterable<? extends Condition> conditions, StateDefinition<Block, BlockState> stateContainer
    ) {
        ArrayList<Predicate<BlockState>> list = new ArrayList<>();
        for (Condition cond : conditions) {
            list.add(cond.getPredicate(stateContainer));
        }
        return canonize(list);
    }

    public static <T> List<Predicate<T>> canonize(List<Predicate<T>> input) {
        input.sort(Comparator.comparingInt(Predicate::hashCode));
        if (input instanceof ArrayList) {
            ((ArrayList<Predicate<T>>) input).trimToSize();
        }
        return input;
    }
}
