package malte0811.ferritecore.impl.compactunihex;

import net.minecraft.client.gui.font.providers.UnihexProvider.LineData;
import org.jetbrains.annotations.Range;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CompactByteContents implements LineData {
    private final long upperBytes;
    private final long lowerBytes;

    public CompactByteContents(long upperBytes, long lowerBytes) {
        this.upperBytes = upperBytes;
        this.lowerBytes = lowerBytes;
    }

    public CompactByteContents(byte[] bytes) {
        if (bytes.length != 16)
            throw new IllegalArgumentException();
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        // not really sure why i have to flip these, but it only works if i do, so...
        this.lowerBytes = buffer.getLong();
        this.upperBytes = buffer.getLong();
    }

    @Override
    public int line(@Range(from = 0, to = 15) int index) {
        // index should always be between 0 and 15
        long bytes = index >= Long.BYTES ? upperBytes : lowerBytes;
        int bits = Byte.SIZE * (index % Byte.SIZE);
        // basically the structure is like b7 b6 b5 b4 b3 b2 b1 b0 (little endian)
        return (byte)((bytes >> bits) & 0xFF) << 24; // shift 24 bits to the left (mc does this idk)
    }

    @Override
    public int bitWidth() {
        return Byte.SIZE; // 8
    }
}
