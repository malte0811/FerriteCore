package malte0811.ferritecore.mixin.nopropertymap;

import com.google.common.collect.ImmutableMap;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

/**
 * Mostly useless. It's not possible to add config options for JS coremods, so I need to supply getValues_Ferrite even
 * if the nopropertymap code is disabled
 */
@Mixin(StateHolder.class)
public abstract class DummyFerriteValuesMixin {
    @Shadow
    public abstract ImmutableMap<Property<?>, Comparable<?>> getValues();

    // Used by JS coremod
    public Map<Property<?>, Comparable<?>> getValues_Ferrite() {
        return getValues();
    }
}
