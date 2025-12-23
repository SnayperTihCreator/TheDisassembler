package snaypertihcreator.thedisassembler.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.Tier1DisassemblerMenu;
import java.util.stream.IntStream;

public class Tier1DisassemblerBlockEntity extends DisassemblerBlockEntity {

    private int isValidRecipe = 0;

    public Tier1DisassemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocksEntity.TIER1_DISASSEMBLER_BE.get(), pos, state, 10);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> Tier1DisassemblerBlockEntity.this.progress;
                    case 1 -> Tier1DisassemblerBlockEntity.this.maxProgress;
                    case 2 -> Tier1DisassemblerBlockEntity.this.isValidRecipe;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> Tier1DisassemblerBlockEntity.this.progress = value;
                    case 1 -> Tier1DisassemblerBlockEntity.this.maxProgress = value;
                    case 2 -> Tier1DisassemblerBlockEntity.this.isValidRecipe = value;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public int getInputSlot() {
        return 0;
    }

    @Override
    public int[] getOutputSlots() {
        return IntStream.range(1, 10).toArray();
    }

    @Override
    protected boolean canAutomationInsert(int slot) {
        return slot == 0;
    }

    @Override
    protected float getLuckModifier() {return -0.01F;}

    @Override
    protected boolean isAutomatic() {return false;}

    public void spined() {
        if (level == null || level.isClientSide) return;
        boolean canWork = canDisassembleCurrentItem() && hasFreeOutputSlot();
        if (canWork) {
            this.progress += 5;
            if (this.progress >= this.maxProgress) finishCrafting();
            setChanged();
        }
    }

    @Override
    protected int calculateMaxProgress() {
        return 100;
    }

    @Override
    protected void updateContainerDataTypes() {
        this.isValidRecipe = canDisassembleCurrentItem()?1:0;
    }

    @Override
    protected void regressProgress() {
        if (this.progress > 0) this.progress--;
        if (this.isValidRecipe == 0) this.progress = 0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.%s.base_block".formatted(TheDisassemblerMod.MODID));
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory inventory, @NotNull Player player) {
        return new Tier1DisassemblerMenu(containerID, inventory, this, this.data);
    }
}