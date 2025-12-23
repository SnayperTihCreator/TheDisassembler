package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.blocksEntity.*;

public class DisassemblerBlock extends Block implements EntityBlock {

    private final TierTheDisassembler tier;

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public DisassemblerBlock(TierTheDisassembler tier) {
        super(Properties.of().strength(3.5F).requiresCorrectToolForDrops()
                .lightLevel(state -> state.getValue(LIT) ? 13 : 0)
        );
        this.tier = tier;
        registerDefaultState(stateDefinition.any()
                .setValue(LIT, false)
                .setValue(WORKING, false)
        );
    }

    // состояния блока
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
        builder.add(WORKING);
    }

    // метод для создания блока контролера
    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return tier.create(pos, state);
    }

    // UI при открытии антиверстака
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.sidedSuccess(true);

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof DisassemblerBlockEntity entity)) throw new IllegalStateException("Container provider is missing!");

        NetworkHooks.openScreen((ServerPlayer) player, entity, pos);

        return InteractionResult.sidedSuccess(false);
    }

    // Создание и получение тикера
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (world.isClientSide) return null;

        return tier.getTicker(type);
    }

    // Удаление антиверстака
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !isMoving) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DisassemblerBlockEntity) dropContents(world, pos, be);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    // выкидываем предметы в мир
    private void dropContents(Level world, BlockPos pos, BlockEntity be) {
        if (!world.isClientSide) {
            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                SimpleContainer container = new SimpleContainer(handler.getSlots());
                for (int i = 0; i < handler.getSlots(); i++) container.setItem(i, handler.getStackInSlot(i));
                Containers.dropContents(world, pos, container);
            });
            if (be instanceof DisassemblerBlockEntity disassembler) disassembler.clearContent();
        }
    }
}
