package malte0811.ferritecore;

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
