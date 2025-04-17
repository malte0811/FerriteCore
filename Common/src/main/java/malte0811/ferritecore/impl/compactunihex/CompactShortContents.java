package malte0811.ferritecore.impl.compactunihex;

import net.minecraft.client.gui.font.providers.UnihexProvider.LineData;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CompactShortContents implements LineData {
    private final long shorts1;
    private final long shorts2;
    private final long shorts3;
    private final long shorts4;

    public CompactShortContents(short[] shorts) {
        if (shorts.length != 16)
            throw new IllegalArgumentException();
        ByteBuffer buffer = ByteBuffer.allocate(16 * Short.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        buffer.asShortBuffer().put(shorts);
        this.shorts1 = buffer.getLong();
        this.shorts2 = buffer.getLong();
        this.shorts3 = buffer.getLong();
        this.shorts4 = buffer.getLong();
    }

    @Override
    public int line(@Range(from = 0, to = 15) int index) {
        // index should always be between 0 and 15
        long shorts = switch (index) {
            case 0, 1, 2, 3 -> shorts1;
            // 4 shorts in a long
            case 4, 5, 6, 7 -> shorts2;
            case 8, 9, 10, 11 -> shorts3;
            case 12, 13, 14, 15 -> shorts4;
            default -> throw new IllegalArgumentException();
        };
        int bits = Short.SIZE * (index % 4);
        // basically the structure is like s3 s2 s1 s0 (little endian)
        return (short)((shorts >> bits) & 0xFFFF) << 16; // shift 16 bits to the left (mc does this idk)
    }

    @Override
    public int bitWidth() {
        return Short.SIZE;
    }
}
