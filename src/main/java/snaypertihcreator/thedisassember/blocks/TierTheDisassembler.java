package snaypertihcreator.thedisassember.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import snaypertihcreator.thedisassember.blocksEntity.*;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public enum TierTheDisassembler {
    BASIC(1, ModBlocksEntity.TIER1_DISASSEMBLER_BE, Tier1DisassemblerBlockEntity::new),
    ADVANCED(2, ModBlocksEntity.TIER2_DISASSEMBLER_BE, Tier2DisassemblerBlockEntity::new),
    PROGRESSIVE(3, ModBlocksEntity.TIER3_DISASSEMBLER_BE, Tier3DisassemblerBlockEntity::new);

    private final int level;
    private final Supplier<? extends BlockEntityType<? extends DisassemblerBlockEntity>> typeSupplier;
    private final BiFunction<BlockPos, BlockState, BlockEntity> factory;

    TierTheDisassembler(int level,
                        Supplier<? extends BlockEntityType<? extends DisassemblerBlockEntity>> typeSupplier,
                        BiFunction<BlockPos, BlockState, BlockEntity> factory) {
        this.level = level;
        this.typeSupplier = typeSupplier;
        this.factory = factory;
    }

    public int getLevel() {
        return level;
    }

    public BlockEntity create(BlockPos pos, BlockState state) {
        return this.factory.apply(pos, state);
    }

    // ВАЖНО: Универсальный метод тикера
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(BlockEntityType<T> actualType) {
        if (actualType == this.typeSupplier.get()) {
            return (BlockEntityTicker<T>) (BlockEntityTicker<? extends DisassemblerBlockEntity>) DisassemblerBlockEntity::tick;
        }
        return null;
    }
}