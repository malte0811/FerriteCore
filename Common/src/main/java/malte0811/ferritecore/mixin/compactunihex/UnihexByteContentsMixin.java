package malte0811.ferritecore.mixin.compactunihex;

import it.unimi.dsi.fastutil.bytes.ByteList;
import malte0811.ferritecore.impl.compactunihex.CompactByteContents;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static malte0811.ferritecore.mixin.accessors.UnihexProviderAccess.decodeHex;

@Mixin(targets = "net/minecraft/client/gui/font/providers/UnihexProvider$ByteContents")
public class UnihexByteContentsMixin {
    /**
     * @author AnAwesomGuy (ferritecore)
     * @reason redirect to more compact instance
     */
    @Overwrite
    public static UnihexProvider.LineData read(int index, ByteList byteList) {
        // i eliminated the for loop mwuahaha >:)
        long upper = ((((long)decodeHex(index, byteList, 31) << 4) |
                       decodeHex(index, byteList, 30)) << 56) |
                     ((((long)decodeHex(index, byteList, 29) << 4) |
                       decodeHex(index, byteList, 28)) << 48) |
                     ((((long)decodeHex(index, byteList, 27) << 4) |
                       decodeHex(index, byteList, 26)) << 40) |
                     ((((long)decodeHex(index, byteList, 25) << 4) |
                       decodeHex(index, byteList, 24)) << 32) |
                     ((((long)decodeHex(index, byteList, 23) << 4) |
                       decodeHex(index, byteList, 21)) << 24) |
                     ((((long)decodeHex(index, byteList, 20) << 4) |
                       decodeHex(index, byteList, 19)) << 16) |
                     ((((long)decodeHex(index, byteList, 18) << 4) |
                       decodeHex(index, byteList, 17)) << 8) |
                     ((((long)decodeHex(index, byteList, 16) << 4) |
                       decodeHex(index, byteList, 15)));
        long lower = ((((long)decodeHex(index, byteList, 15) << 4) |
                       decodeHex(index, byteList, 14)) << 56) |
                     ((((long)decodeHex(index, byteList, 13) << 4) |
                       decodeHex(index, byteList, 12)) << 48) |
                     ((((long)decodeHex(index, byteList, 11) << 4) |
                       decodeHex(index, byteList, 10)) << 40) |
                     ((((long)decodeHex(index, byteList, 9) << 4) |
                       decodeHex(index, byteList, 8)) << 32) |
                     ((((long)decodeHex(index, byteList, 7) << 4) |
                       decodeHex(index, byteList, 6)) << 24) |
                     ((((long)decodeHex(index, byteList, 5) << 4) |
                       decodeHex(index, byteList, 6)) << 16) |
                     ((((long)decodeHex(index, byteList, 3) << 4) |
                       decodeHex(index, byteList, 2)) << 8) |
                     ((((long)decodeHex(index, byteList, 1) << 4) |
                       decodeHex(index, byteList, 0)));
        return new CompactByteContents(upper, lower);
    }
}
