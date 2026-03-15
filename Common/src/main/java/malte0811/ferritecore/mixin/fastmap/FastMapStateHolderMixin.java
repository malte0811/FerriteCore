package malte0811.ferritecore.mixin.fastmap;

import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.fastmap.FastMap;
import malte0811.ferritecore.impl.FastMapStateHolderImpl;
import malte0811.ferritecore.mixin.config.FerriteConfig;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Applied before other Mixins to avoid overwriting injected code
@Mixin(value = StateHolder.class, priority = 900)
public abstract class FastMapStateHolderMixin<O, S> implements FastMapStateHolder<S> {
    @Shadow
    @Final
    @Mutable
    private Comparable<?>[] propertyValues;
    @Shadow
    @Final
    protected O owner;

    @Shadow
    public abstract boolean isSingletonState();

    @Unique
    private int ferritecore_globalTableIndex;
    @Unique
    private FastMap<S> ferritecore_globalTable;

    /**
     * @author malte811
     * @reason We need to replace the "else" branch, the rest stays the same. Ideally, this would be a multidimensional
     * array access redirect, but those seem to be broken (result in bytecode verifier errors).
     */
    @Overwrite
    private <T extends Comparable<T>, V extends T> S setValueInternal(Property<T> property, int propertyIndex, V value) {
        int valueIndex = property.getInternalIndex(value);
        if (valueIndex < 0) {
            throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + this.owner + ", it is not an allowed value");
        } else {
            return ferritecore_globalTable.with(this.ferritecore_globalTableIndex, propertyIndex, valueIndex);
        }
    }

    /**
     * @reason This Mixin completely replaces the neighbor data structure to reduce memory usage
     * @author malte0811
     */
    @Overwrite
    void initializeNeighbors(S[][] neighbors) {
        if (!this.isSingletonState()) {
            throw new UnsupportedOperationException("Neighbor arrays are replaced by FerriteCore. This function should only be called for singleton states.");
        }
    }

    @Redirect(
            method = {"getNullableValue", "lambda$getValues$0"},
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/block/state/StateHolder;propertyValues:[Ljava/lang/Comparable;",
                    opcode = Opcodes.GETFIELD,
                    args = "array=get"
            )
    )
    private Comparable<?> redirectPropertyValueAccess(Comparable<?>[] values, int index) {
        return FastMapStateHolderImpl.getPropertyValue(
                values, ferritecore_globalTable, ferritecore_globalTableIndex, index
        );
    }

    @Override
    public void ferritecore_setStateMap(FastMap<S> stateMap, int tableIndex) {
        ferritecore_globalTable = stateMap;
        ferritecore_globalTableIndex = tableIndex;
        if (FerriteConfig.PROPERTY_MAP.isEnabled()) {
            this.propertyValues = null;
        }
    }
}
