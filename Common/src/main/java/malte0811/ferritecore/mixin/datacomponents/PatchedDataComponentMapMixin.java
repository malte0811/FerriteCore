package malte0811.ferritecore.mixin.datacomponents;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PatchedDataComponentMap.class)
public class PatchedDataComponentMapMixin {
    @Shadow
    private Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch;

    @Shadow
    private boolean copyOnWrite;

    @Inject(
            method = {"applyPatch(Lnet/minecraft/core/component/DataComponentPatch;)V", "restorePatch"},
            at = @At("RETURN")
    )
    private void saveMemoryIfEmpty(CallbackInfo ci) {
        if (patch.isEmpty()) {
            // Use a singleton empty map to reduce memory overhead from empty maps, use copyOnWrite to ensure we never
            // try to modify this map
            this.patch = Reference2ObjectMaps.emptyMap();
            this.copyOnWrite = true;
        }
    }

    // Mixin seems to require an injection into a non-void method to take CallbackInfoReturnable rather than
    // CallbackInfo, so we need a separate method to inject into set and remove.
    @Inject(method = {"set", "remove"}, at = @At("RETURN"))
    private void saveMemoryIfEmptyWithReturn(CallbackInfoReturnable<?> ci) {
        saveMemoryIfEmpty(ci);
    }
}
