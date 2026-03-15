package malte0811.ferritecore.ducks;

import malte0811.ferritecore.fastmap.FastMap;

public interface FastMapStateHolder<S> {
    void ferritecore_setStateMap(FastMap<S> stateMap, int tableIndex);
}
