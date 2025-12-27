package snaypertihcreator.thedisassembler.blocksEntity.distillation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.CoalExtractorBlock;
import snaypertihcreator.thedisassembler.menus.distillation.CoalExtractorMenu;
import snaypertihcreator.thedisassembler.menus.distillation.PrimitiveExtractorMenu;

public class CoalExtractorBlockEntity extends ExtractorBlockEntity {
    private int burnTime;
    private int burnDuration;


    @Override
    protected boolean serverTickFuel() {
        boolean dirty = false;

        // 1. Сжигаем текущее топливо
        if (this.burnTime > 0) {
            this.burnTime--;
            dirty = true;
        }

        // 2. Если топливо кончилось, но нужно работать - подкидываем новое
        if (this.burnTime <= 0 && canDisassembleCurrentItem() && hasFreeOutputSlot() && hasRequiredTools()) {
            ItemStack fuelStack = handler.getStackInSlot(1);
            if (!fuelStack.isEmpty()) {
                int fuelTime = ForgeHooks.getBurnTime(fuelStack, null);
                if (fuelTime > 0) {
                    this.burnTime = fuelTime;
                    this.burnDuration = fuelTime;

                    ItemStack remainder = fuelStack.getCraftingRemainingItem();
                    fuelStack.shrink(1);
                    if (fuelStack.isEmpty()) handler.setStackInSlot(1, remainder);
                    dirty = true;
                }
            }
        }

        if (dirty) setChanged();
        return this.burnTime > 0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.%s.coal_extractor".formatted(TheDisassemblerMod.MODID));
    }

    @Override
    protected float getTargetTemperature() {
        var isOpen = getBlockState().getValue(CoalExtractorBlock.OPEN);
        if (!isOpen){
            return super.getTargetTemperature();
        }
        if (burnTime > 0){
            return 600f;
        }
        return super.getTargetTemperature();
    }

    @Override
    protected float getHeatSpeed() {
        var isOpen = getBlockState().getValue(CoalExtractorBlock.OPEN);
        if (isOpen){

        }
    }

    @Override
    protected float getCoolingSpeed() {
        return 0.3f;
    }

    @Override
    protected int getOutputSlot() {
        return 3;
    }

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
                    case 4 -> burnTime;
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
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("burnDuration", burnDuration);
    }

    // загрузка состояния
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        burnTime = nbt.getInt("burnTime");
        burnDuration = nbt.getInt("burnDuration");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory inv, @NotNull Player player) {
        return new CoalExtractorMenu(containerID, inv, this, this.data);
    }
}
