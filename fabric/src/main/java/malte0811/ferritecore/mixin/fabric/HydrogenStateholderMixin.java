package malte0811.ferritecore.mixin.fabric;

import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

// This mixin is conditionally added by the Mixin config
@SuppressWarnings("UnusedMixin")
@Mixin(value = StateHolder.class, priority = 900)
public class HydrogenStateholderMixin<S> {
    /**
     * Disable the callback injected by Hydrogen, since it relies on the neighbor table being present, which is not the
     * case after this method is called with FASTMAP enabled.<br>
     * This is a massive hack, but is currently probably the cleanest approach. If H gets the system for
     * disabling Mixins already implemented in Na/Li that system should be used instead!
     */
    @Inject(method = "populateNeighbours", at = @At("RETURN"), cancellable = true)
    public void postPopulateNeighbors(Map<Map<Property<?>, Comparable<?>>, S> map, CallbackInfo ci) {
        ci.cancel();
    }
}
