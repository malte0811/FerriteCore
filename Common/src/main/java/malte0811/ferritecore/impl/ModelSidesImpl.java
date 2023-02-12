package malte0811.ferritecore.impl;

import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Use immutable lists for all quad lists. The backing array will exactly match the list size, and all empty lists will
 * use the same list instance. Additionally, the shallow size will be smaller than that of an ArrayList (otherwise
 * ArrayList#trimToSize could be used)
 */
public class ModelSidesImpl {
    private static final Direction[] SIDES = Direction.values();
    private static final Map<Direction, List<BakedQuad>> EMPTY = Util.make(new EnumMap<>(Direction.class), m -> {
        for (Direction side : SIDES) {
            m.put(side, List.of());
        }
    });

    public static List<BakedQuad> minimizeUnculled(List<BakedQuad> quads) {
        return List.copyOf(quads);
    }

    public static Map<Direction, List<BakedQuad>> minimizeCulled(Map<Direction, List<BakedQuad>> quadsBySide) {
        if (quadsBySide.isEmpty()) {
            // Workaround: Forge's EmptyModel does this, I'm quite sure that it would crash if it was actually used
            // anywhere
            return quadsBySide;
        }
        boolean allEmpty = true;
        for (final var face : SIDES) {
            final var sideQuads = quadsBySide.get(face);
            quadsBySide.put(face, List.copyOf(sideQuads));
            allEmpty &= sideQuads.isEmpty();
        }
        return allEmpty ? EMPTY : quadsBySide;
    }
}
