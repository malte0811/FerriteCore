package malte0811.ferritecore.mixin.fastmap;

import com.google.common.collect.ImmutableMap;
import malte0811.ferritecore.FastMap;
import malte0811.ferritecore.HackyGlobalState;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.ducks.NoPropertyStateHolder;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;
import org.spongepowered.asm.mixin.*;

import java.util.Collection;
import java.util.Map;

@Mixin(StateHolder.class)
public abstract class FastMapStateHolderMixin<O, S> implements FastMapStateHolder<S> {
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
        if (this instanceof NoPropertyStateHolder) {
            properties = null;
        }
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

    @Override
    public FastMap<S> getStateMap() {
        return globalTable;
    }

    @Override
    public int getStateIndex() {
        return globalTableIndex;
    }
}
