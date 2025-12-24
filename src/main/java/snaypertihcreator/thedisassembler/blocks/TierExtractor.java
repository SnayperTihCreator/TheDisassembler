package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import snaypertihcreator.thedisassembler.blocksEntity.ModBlocksEntity;
import snaypertihcreator.thedisassembler.blocksEntity.distillation.ExtractorBlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.distillation.PrimitiveExtractorBlockEntity;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public enum TierExtractor {

    PRIMITIVE(1, ModBlocksEntity.TIER1_DISTILLATION_BE, PrimitiveExtractorBlockEntity::new);
    // 1. Примитивный: 3 слота, медленно греется, без топлива внутри
//    PRIMITIVE(0.5f, false, ModBlockEntities.PRIMITIVE_EXTRACTOR, PrimitiveExtractorBlockEntity::new),
    // 2. Угольный: 4 слота, средний нагрев, есть топливо
//    COAL(1.5f, true, ModBlockEntities.COAL_EXTRACTOR, CoalExtractorBlockEntity::new);
    ;

    // --- Поля Баланса ---
    private final int level;
    private final Supplier<? extends BlockEntityType<? extends ExtractorBlockEntity>> typeSupplier;
    private final BiFunction<BlockPos, BlockState, ExtractorBlockEntity> factory;

    // --- Конструктор ---
    TierExtractor(int level,
                  Supplier<? extends BlockEntityType<? extends ExtractorBlockEntity>> typeSupplier,
                  BiFunction<BlockPos, BlockState, ExtractorBlockEntity> factory) {
        this.level = level;
        this.typeSupplier = typeSupplier;
        this.factory = factory;
    }

    public int getLevel() {
        return level;
    }

    // --- Фабрика (для newBlockEntity в блоке) ---
    public BlockEntity create(BlockPos pos, BlockState state) {
        return this.factory.apply(pos, state);
    }

    // --- Тикер (для getTicker в блоке) ---
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(BlockEntityType<T> actualType) {
        if (actualType == this.typeSupplier.get()) return (BlockEntityTicker<T>) (BlockEntityTicker<? extends ExtractorBlockEntity>) ExtractorBlockEntity::tick;
        return null;
    }
}