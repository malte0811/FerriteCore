package malte0811.ferritecore.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface CollectionUtil {
    @SuppressWarnings("unchecked")
    static <E> List<E> minimize(List<E> l) {
        if (l.isEmpty())
            return List.of();
        else if (l.size() < 3) // java provides elements in fields for 1, 2 sizes
            return List.copyOf(l);

        if (l instanceof ArrayList<E>)
            ((ArrayList<E>)l).trimToSize();
        if (l instanceof ObjectArrayList<E>)
            ((ObjectArrayList<E>)l).trim();
        else
            return Arrays.asList((E[])l.toArray());

        return l;
    }
}
