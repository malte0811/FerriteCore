package malte0811.ferritecore.util;

import malte0811.ferritecore.IPlatformHooks;

public class Constants {
    public static final String MODID = "ferritecore";
    public static final IPlatformHooks PLATFORM_HOOKS;

    static {
        try {
            Class<?> hooks = Class.forName("malte0811.ferritecore.PlatformHooks");
            PLATFORM_HOOKS = (IPlatformHooks) hooks.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
