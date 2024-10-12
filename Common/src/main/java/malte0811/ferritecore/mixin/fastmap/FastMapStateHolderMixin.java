package malte0811.ferritecore.mixin.fastmap;

import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import malte0811.ferritecore.ducks.FastMapStateHolder;
import malte0811.ferritecore.fastmap.FastMap;
import malte0811.ferritecore.impl.StateHolderImpl;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(StateHolder.class)
public abstract class FastMapStateHolderMixin<O, S> implements FastMapStateHolder<S> {
    @Mutable
    @Shadow
    @Final
    private Reference2ObjectArrayMap<Property<?>, Comparable<?>> values;
    @Shadow
    private Table<Property<?>, Comparable<?>, S> neighbours;

    private int ferritecore_globalTableIndex;
    private FastMap<S> ferritecore_globalTable;

    @Redirect(
            method = {"setValue", "trySetValue"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Table;get(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    remap = false
            )
    )
    public Object getNeighborFromFastMap(Table<?, ?, ?> ignore, Object rowKey, Object columnKey) {
        return this.ferritecore_globalTable.with(
                this.ferritecore_globalTableIndex,
                (Property<?>) rowKey,
                columnKey
        );
    }

    /**
     * @reason This Mixin completely replaces the data structures initialized by this method, as the original ones waste
     * a lot of memory
     * @author malte0811
     */
    @Overwrite
    public void populateNeighbours(Map<Map<Property<?>, Comparable<?>>, S> states) {
        StateHolderImpl.populateNeighbors(states, this);
    }

    @Override
    public FastMap<S> getStateMap() {
        return ferritecore_globalTable;
    }

    @Override
    public int getStateIndex() {
        return ferritecore_globalTableIndex;
    }

    @Override
    public Reference2ObjectMap<Property<?>, Comparable<?>> getVanillaPropertyMap() {
        return values;
    }

    @Override
    public void replacePropertyMap(Reference2ObjectMap<Property<?>, Comparable<?>> newMap) {
        // This cast is incorrect and will be removed by FerriteMixinConfig#postApply when the field type is changed to
        // Reference2ObjectMap
        values = (Reference2ObjectArrayMap<Property<?>, Comparable<?>>) newMap;
    }

    @Override
    public void setStateMap(FastMap<S> newValue) {
        ferritecore_globalTable = newValue;
    }

    @Override
    public void setStateIndex(int newValue) {
        ferritecore_globalTableIndex = newValue;
    }

    @Override
    public void setNeighborTable(Table<Property<?>, Comparable<?>, S> table) {
        neighbours = table;
    }

    @Override
    public Table<Property<?>, Comparable<?>, S> getNeighborTable() {
        return neighbours;
    }
}
