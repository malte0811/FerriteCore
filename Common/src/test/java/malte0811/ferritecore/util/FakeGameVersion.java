package malte0811.ferritecore.util;

import net.minecraft.WorldVersion;
import net.minecraft.world.level.storage.DataVersion;

import java.util.Date;

public class FakeGameVersion implements WorldVersion {
    @Override
    public DataVersion getDataVersion() {
        return new DataVersion(0);
    }

    @Override
    public String getId() {
        return "test";
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getReleaseTarget() {
        return "none";
    }

    @Override
    public int getProtocolVersion() {
        return 0;
    }

    @Override
    public Date getBuildTime() {
        return new Date(0);
    }

    @Override
    public boolean isStable() {
        return false;
    }
}
