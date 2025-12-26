package snaypertihcreator.thedisassembler.blocksEntity.distillation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocksEntity.ModBlocksEntity;
import snaypertihcreator.thedisassembler.menus.distillation.PrimitiveExtractorMenu;

public class PrimitiveExtractorBlockEntity extends ExtractorBlockEntity{
    public PrimitiveExtractorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlocksEntity.TIER1_DISTILLATION_BE.get(), pos, state, 3);
    }

    @Override
    protected float getTargetTemperature() {
        if (level == null) return 20.0f;

        float biomeRawTemp = level.getBiome(worldPosition).value().getBaseTemperature();

        ResourceLocation dimId = level.dimension().location();

        float baseAmbientTemp;
        if (dimId.equals(Level.NETHER.location())) {
            baseAmbientTemp = 90.0f + (biomeRawTemp * 12.5f); // Жар Незера: 90°C - 115°C
        } else if (dimId.equals(Level.END.location())) {
            baseAmbientTemp = -40.0f; // Холод Энда: стабильные -40°C
        } else {
            baseAmbientTemp = (biomeRawTemp - 0.15f) * 35.0f + 12.0f; // Обычный мир: от -15°C до +60°C или другие
        }

        BlockPos belowPos = worldPosition.below();
        BlockState belowState = level.getBlockState(belowPos);

        // 2. Проверяем источники тепла
        if (belowState.is(Blocks.LAVA) || belowState.getFluidState().getType() == Fluids.LAVA) return 500.0f;
        if (belowState.is(Blocks.MAGMA_BLOCK)) return 350.0f;
        if (belowState.is(Blocks.FIRE)) return 180.0f;
        if (belowState.is(Blocks.SOUL_FIRE)) return 280.0f;

        if (belowState.is(Blocks.CAMPFIRE)) {
            if (belowState.hasProperty(CampfireBlock.LIT) && belowState.getValue(CampfireBlock.LIT)) return 150.0f;
        }

        if (belowState.is(Blocks.SOUL_CAMPFIRE)) {
            if (belowState.hasProperty(CampfireBlock.LIT) && belowState.getValue(CampfireBlock.LIT)) return 250.0f;
        }

        if (dimId.equals(Level.OVERWORLD.location()) && level.isRainingAt(worldPosition)) {
            baseAmbientTemp -= 15.0f;
        }
        return baseAmbientTemp;
    }

    @Override
    protected float getHeatSpeed() {
        if (level == null) return 0.5f;
        ResourceLocation dimId = level.dimension().location();

        if (dimId.equals(Level.NETHER.location())) return 1.2f; // Нагревается очень быстро
        if (dimId.equals(Level.END.location())) return 0.2f;    // В пустоте трудно нагреть блок

        return 0.5f;
    }

    @Override
    protected float getCoolingSpeed() {
        if (level == null) return 0.3f;
        ResourceLocation dimId = level.dimension().location();

        if (dimId.equals(Level.NETHER.location())) return 0.05f; // Почти не остывает сам по себе
        if (dimId.equals(Level.END.location())) return 1.5f;    // Моментально теряет тепло в вакуум

        return 0.3f; // Обычная скорость остывания
    }

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
                    case 3 -> (cachedRecipe != null) ? (int) cachedRecipe.getTemperature() : 0;
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
                return 4;
            }
        };
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.%s.primitive_extractor".formatted(TheDisassemblerMod.MODID));
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory inv, @NotNull Player player) {
        return new PrimitiveExtractorMenu(containerID, inv, this, this.data);
    }
}
