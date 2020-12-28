package malte0811.ferritecore.ducks;

import malte0811.ferritecore.FastMap;

public interface FastMapStateHolder<S> {
    FastMap<S> getStateMap();

    int getStateIndex();
}
