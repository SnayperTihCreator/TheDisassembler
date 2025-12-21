package snaypertihcreator.thedisassember.blocks;

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
import snaypertihcreator.thedisassember.blocksEntity.DisassemblerBlockEntity;
import snaypertihcreator.thedisassember.blocksEntity.ModBlocksEntity;
import snaypertihcreator.thedisassember.blocksEntity.Tier1DisassemblerBlockEntity;
import snaypertihcreator.thedisassember.blocksEntity.Tier2DisassemblerBlockEntity;

public class DisassemberBlock extends Block implements EntityBlock {
    private final TierTheDisassember tier;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public DisassemberBlock(TierTheDisassember tier) {
        super(Properties.of()
                .strength(3.5F)
                .requiresCorrectToolForDrops()
        );
        this.tier = tier;
        registerDefaultState(getStateDefinition().any().setValue(LIT, false));
    }

    // состояния блока
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    // метод для создания блока контролера
    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return switch (tier.getLevel()){
            case 1 -> new Tier1DisassemblerBlockEntity(pos, state);
            case 2 -> new Tier2DisassemblerBlockEntity(pos, state);
            default -> throw new IllegalStateException("Unexpected value: " + tier.getLevel());
        };
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

        if (type == ModBlocksEntity.TIER1_DISASSEMBER_BE.get())
            return createTickerHelper(type, ModBlocksEntity.TIER1_DISASSEMBER_BE.get(), Tier1DisassemblerBlockEntity::tick);
        if (type == ModBlocksEntity.TIER2_DISASSEMBER_BE.get())
            return createTickerHelper(type, ModBlocksEntity.TIER2_DISASSEMBER_BE.get(), Tier2DisassemblerBlockEntity::tick);
        return null;

    }

    // Удаление антиверстака
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !isMoving) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof DisassemblerBlockEntity) {
                dropContents(world, pos, be);
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    // выкидываем предметы в мир
    private void dropContents(Level world, BlockPos pos, BlockEntity be) {
        if (!world.isClientSide) {
            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                SimpleContainer container = new SimpleContainer(handler.getSlots());
                for (int i = 0; i < handler.getSlots(); i++) {
                    container.setItem(i, handler.getStackInSlot(i));
                }
                Containers.dropContents(world, pos, container);
            });
            if (be instanceof DisassemblerBlockEntity disassembler) {
                disassembler.clearContent();
            }
        }
    }

    // вспомагалка для типов
    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actualType, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker) {
        return targetType == actualType ? (BlockEntityTicker<A>) ticker : null;
    }
}
