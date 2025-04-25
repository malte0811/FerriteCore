package malte0811.ferritecore.mixin.suffixarray;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import malte0811.ferritecore.impl.CharListIntListWrapper;
import net.minecraft.client.searchtree.SuffixArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SuffixArray.class)
public class SuffixArrayMixin {
    @Mutable
    @Shadow
    @Final
    private IntList chars;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void wrapCharListAsIntList(CallbackInfo ci) {
        chars = new CharListIntListWrapper(new CharArrayList());
    }
}
