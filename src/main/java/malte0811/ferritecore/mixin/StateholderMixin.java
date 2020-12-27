package malte0811.ferritecore.mixin;

import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.FastMap;
import malte0811.ferritecore.HackyGlobalState;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Mixin(StateHolder.class)
public abstract class StateholderMixin<O, S> {
    @Mutable
    @Shadow
    @Final
    private ImmutableMap<Property<?>, Comparable<?>> properties;
    @Shadow
    @Final
    protected O instance;

    @Shadow
    public abstract Collection<Property<?>> getProperties();

    @Shadow
    public abstract <T extends Comparable<T>> T get(Property<T> property);

    private int globalTableIndex;
    private FastMap<S> globalTable;

    /**
     * @reason This Mixin completely replaces the data structures initialized by this method, as the original ones waste
     * a lot of memory
     * @author malte0811
     */
    @Overwrite
    public void func_235899_a_(Map<Map<Property<?>, Comparable<?>>, S> states) {
        if (globalTable != null) {
            throw new IllegalStateException();
        } else if (states == HackyGlobalState.LAST_STATE_MAP.get()) {
            // Use "hacky global state" to use the same fast map for all states of one block
            this.globalTable = (FastMap<S>) HackyGlobalState.LAST_FAST_STATE_MAP.get();
        } else {
            HackyGlobalState.LAST_STATE_MAP.set(states);
            this.globalTable = new FastMap<>(getProperties(), states);
            HackyGlobalState.LAST_FAST_STATE_MAP.set(this.globalTable);
        }
        this.globalTableIndex = this.globalTable.getIndexOf(properties);
        properties = null;
    }

    /**
     * @reason The original implementation relies on the vanilla neighbor data, which is never present with these Mixins
     * @author malte0811
     */
    @Overwrite
    public <T extends Comparable<T>, V extends T> S with(Property<T> property, V value) {
        Comparable<?> comparable = get(property);
        if (comparable == value) {
            return (S) this;
        } else {
            S s = this.globalTable.with(this.globalTableIndex, property, value);
            if (s == null) {
                throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + this.instance + ", it is not an allowed value");
            } else {
                return s;
            }
        }
    }

    // All other Mixins: If the new data structures are initialized, use those. Otherwise (if populateNeighbors didn't
    // run yet) use the vanilla code using `properties`
    @Redirect(
            method = {"get", "func_235903_d_"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap;get(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    @Coerce
    public Object getValueOfProperty(ImmutableMap<?, ?> vanillaMap, Object key) {
        if (globalTable != null) {
            return globalTable.getValue(globalTableIndex, (Property<?>) key);
        } else {
            return vanillaMap.get(key);
        }
    }

    // TODO speed up in some way?
    // The cleanest (lowest-impact) approach would be to use a custom implementation of ImmutableMap (based on a FastMap
    // and an index), but that whole class hierarchy is a very "closed" (many essential methods/classes are
    // package-private)
    @Inject(method = "getValues", at = @At("HEAD"), cancellable = true)
    public void getValuesHead(CallbackInfoReturnable<ImmutableMap<Property<?>, Comparable<?>>> cir) {
        if (globalTable != null) {
            ImmutableMap.Builder<Property<?>, Comparable<?>> result = ImmutableMap.builder();
            for (Property<?> p : globalTable.getProperties()) {
                result.put(p, get(p));
            }
            cir.setReturnValue(result.build());
            cir.cancel();
        }
    }

    @Inject(method = "hasProperty", at = @At("HEAD"), cancellable = true)
    public <T extends Comparable<T>>
    void hasPropertyHead(Property<T> property, CallbackInfoReturnable<Boolean> cir) {
        if (globalTable != null) {
            cir.setReturnValue(globalTable.getProperties().contains(property));
            cir.cancel();
        }
    }

    @Inject(method = "getProperties", at = @At("HEAD"), cancellable = true)
    public void getPropertiesHead(CallbackInfoReturnable<Collection<Property<?>>> cir) {
        if (globalTable != null) {
            cir.setReturnValue(globalTable.getProperties());
            cir.cancel();
        }
    }
}
