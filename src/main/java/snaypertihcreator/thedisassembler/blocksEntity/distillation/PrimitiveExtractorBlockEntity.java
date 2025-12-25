package snaypertihcreator.thedisassembler.blocksEntity.distillation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocksEntity.ModBlocksEntity;

public class PrimitiveExtractorBlockEntity extends ExtractorBlockEntity{
    public PrimitiveExtractorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlocksEntity.TIER1_DISTILLATION_BE.get(), pos, state, 3);
    }

    @Override
    protected float getTargetTemperature() {
        if (level == null) return 20.0f;

        BlockPos belowPos = worldPosition.below();
        BlockState belowState = level.getBlockState(belowPos);

        if (belowState.is(Blocks.LAVA) || belowState.getFluidState().getType() == Fluids.LAVA) return 500.0f;
        if (belowState.is(Blocks.MAGMA_BLOCK)) return 350.0f;
        if (belowState.is(Blocks.FIRE) || belowState.is(Blocks.SOUL_FIRE)) return 400.0f;
        if (belowState.is(Blocks.CAMPFIRE)) {
            if (belowState.hasProperty(CampfireBlock.LIT) && belowState.getValue(CampfireBlock.LIT)) return 150.0f;
        }

        if (belowState.is(Blocks.SOUL_CAMPFIRE)) {
            if (belowState.hasProperty(CampfireBlock.LIT) && belowState.getValue(CampfireBlock.LIT)) return 250.0f;
        }
        return 20.0f;
    }

    @Override
    protected float getHeatSpeed() {return 0.5f;}

    @Override
    protected int getOutputSlot() {return 2;}

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> (int) currentTemp;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> currentTemp = value;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.%s.primitive_extractor".formatted(TheDisassemblerMod.MODID));
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory inv, @NotNull Player player) {
        return null; // TODO написать меню и GUI к этому блоку (макс температуру взять за 500)
    }
}
