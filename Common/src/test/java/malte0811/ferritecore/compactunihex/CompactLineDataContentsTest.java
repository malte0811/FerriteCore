package malte0811.ferritecore.compactunihex;

import malte0811.ferritecore.impl.compactunihex.CompactByteContents;
import malte0811.ferritecore.impl.compactunihex.CompactShortContents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompactLineDataContentsTest {
    @Test
    public void testShortContents() {
        short[] shorts = new short[]{
            0x1010, 0x2020, 0x3030, 0x4040, 0x5050, 0x6060, 0x7070, (short)0x8080,
            0x1111, 0x2222, 0x3333, 0x4444, 0x5555, 0x6666, 0x7777, (short)0x8888
        };
        CompactShortContents compactShortContents = new CompactShortContents(shorts);
        for (int i = 0; i < shorts.length; i++)
            Assertions.assertEquals(shorts[i] << 16, compactShortContents.line(i));

    }

    @Test
    public void testByteContents() {
        byte[] bytes = new byte[]{
            0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, (byte)0x80,
            0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte)0x88
        };
        CompactByteContents compactByteContents = new CompactByteContents(bytes);
        for (int i = 0; i < bytes.length; i++)
            Assertions.assertEquals(bytes[i] << 24, compactByteContents.line(i));
    }
}
