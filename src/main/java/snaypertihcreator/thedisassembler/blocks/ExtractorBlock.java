package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.blocksEntity.distillation.ExtractorBlockEntity;


public class ExtractorBlock extends Block implements EntityBlock {

    // Свойства блока
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    private final TierExtractor tier;

    public ExtractorBlock(TierExtractor tier) {
        super(Properties.of()
                .strength(3.5F)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .lightLevel(state -> state.getValue(LIT) ? 13 : 0)
        );
        this.tier = tier;

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(WORKING, false)
        );
    }

    // --- BlockState (Состояния) ---

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, WORKING);
    }

    // Поворот лицом к игроку при установке
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return tier.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return tier.getTicker(type);
    }

    // --- Взаимодействие (GUI) ---

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ExtractorBlockEntity entity)) throw new IllegalStateException("Container provider is missing!");

        NetworkHooks.openScreen((ServerPlayer) player, entity, pos);
        return InteractionResult.sidedSuccess(false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock() && !isMoving) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ExtractorBlockEntity) dropContents(world, pos, be);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    private void dropContents(Level world, BlockPos pos, BlockEntity be) {
        if (!world.isClientSide) {
            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                SimpleContainer container = new SimpleContainer(handler.getSlots());
                for (int i = 0; i < handler.getSlots(); i++) container.setItem(i, handler.getStackInSlot(i));
                Containers.dropContents(world, pos, container);
            });
            if (be instanceof ExtractorBlockEntity disassembler) disassembler.clearContent();
        }
    }
}