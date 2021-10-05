package malte0811.ferritecore.mixin.chunknbt;

import malte0811.ferritecore.impl.ChunkNBTImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {

    @Shadow
    private static void postLoadChunk(CompoundTag compoundTag, LevelChunk levelChunk) {}

    @Inject(
            method = "read",
            at = @At(value = "NEW", target = "net/minecraft/world/level/chunk/LevelChunk")
    )
    private static void extractLevelNBT(
            ServerLevel _1, StructureManager _2, PoiManager _3, ChunkPos _4, CompoundTag fullNBT,
            CallbackInfoReturnable<ProtoChunk> cir
    ) {
        ChunkNBTImpl.extractNBT(fullNBT);
    }

    @ModifyArg(
            method = "read",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/ChunkBiomeContainer;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/level/TickList;Lnet/minecraft/world/level/TickList;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Ljava/util/function/Consumer;)V"),
            index = 8
    )
    private static Consumer<LevelChunk> replacePostLoad(
            Level level, ChunkPos pos, ChunkBiomeContainer biomes, UpgradeData upgradeData, TickList<Block> blockTicks,
            TickList<Fluid> fluidTicks, long l, LevelChunkSection[] sections, Consumer<LevelChunk> consumer
    ) {
        CompoundTag strippedNBT = ChunkNBTImpl.getExtractedNBT();
        return levelChunk -> postLoadChunk(strippedNBT, levelChunk);
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void resetExtractedNBT(
            ServerLevel _1, StructureManager _2, PoiManager _3, ChunkPos _4, CompoundTag _5,
            CallbackInfoReturnable<ProtoChunk> cir
    ) {
        ChunkNBTImpl.clearExtractedNBT();
    }
}
