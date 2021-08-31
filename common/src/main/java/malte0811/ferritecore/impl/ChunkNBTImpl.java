package malte0811.ferritecore.impl;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;

public class ChunkNBTImpl {
    private static final ThreadLocal<CompoundTag> TEMP_LEVEL_NBT = new ThreadLocal<>();

    public static void extractNBT(CompoundTag fullTag) {
        CompoundTag fullLevelNBT = fullTag.getCompound("Level");
        CompoundTag onlyRelevant = new CompoundTag();
        copyTagFrom(fullLevelNBT, onlyRelevant, "Entities");
        copyTagFrom(fullLevelNBT, onlyRelevant, "TileEntities");
        TEMP_LEVEL_NBT.set(onlyRelevant);
    }

    private static void copyTagFrom(CompoundTag from, CompoundTag to, String key) {
        Tag subtag = from.get(key);
        if (subtag != null) {
            to.put(key, subtag);
        }
    }

    public static CompoundTag getExtractedNBT() {
        return Objects.requireNonNull(TEMP_LEVEL_NBT.get());
    }

    public static void clearExtractedNBT() {
        TEMP_LEVEL_NBT.set(null);
    }
}
