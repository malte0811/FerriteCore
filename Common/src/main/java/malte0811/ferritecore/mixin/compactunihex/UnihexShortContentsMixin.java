package malte0811.ferritecore.mixin.compactunihex;

import it.unimi.dsi.fastutil.bytes.ByteList;
import malte0811.ferritecore.impl.compactunihex.CompactShortContents;
import net.minecraft.client.gui.font.providers.UnihexProvider.LineData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net/minecraft/client/gui/font/providers/UnihexProvider$ShortContents")
public class UnihexShortContentsMixin {
    @Inject(method = "read", at = @At(value = "NEW", target = "([S)Lnet/minecraft/client/gui/font/providers/UnihexProvider$ShortContents;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void redirectByteContentsToOurs(int index, ByteList byteList, CallbackInfoReturnable<LineData> cir, short[] bytes, int i) {
        cir.setReturnValue(new CompactShortContents(bytes));
    }
}
