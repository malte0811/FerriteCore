package malte0811.ferritecore;

// Loaded reflectively by Constants under this exact name, so the class and package must not move.
public class PlatformHooks implements IPlatformHooks {
    @Override
    public String computeBlockstateCacheFieldName() {
        return "cache";
    }

    @Override
    public String computeStateHolderValuesName() {
        return "values";
    }
}
