package malte0811.ferritecore.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.multipart.ICondition;
import net.minecraft.state.StateContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class PredicateHelper {
    public static List<Predicate<BlockState>> toCanonicalList(
            Iterable<? extends ICondition> conditions, StateContainer<Block, BlockState> stateContainer
    ) {
        ArrayList<Predicate<BlockState>> list = new ArrayList<>();
        for (ICondition cond : conditions) {
            list.add(cond.getPredicate(stateContainer));
        }
        list.sort(Comparator.comparingInt(Predicate::hashCode));
        list.trimToSize();
        return list;
    }
}
