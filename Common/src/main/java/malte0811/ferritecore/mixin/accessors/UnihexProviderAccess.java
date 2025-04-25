package malte0811.ferritecore.mixin.accessors;

import it.unimi.dsi.fastutil.bytes.ByteList;
import net.minecraft.client.gui.font.providers.UnihexProvider;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(UnihexProvider.class)
public interface UnihexProviderAccess {
    @Contract("_, _, _ -> _") // ij thinks it always throws :(
    @Invoker("decodeHex")
    static int decodeHex(int lineNumber, ByteList byteList, int index) {
        //noinspection Contract
        throw new AssertionError();
    }
}
