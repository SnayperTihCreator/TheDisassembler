package snaypertihcreator.thedisassember.menus;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.blocksEntity.DisassemblerBlockEntity;

public abstract class DisassemblerMenu extends AbstractContainerMenu {

    public final DisassemblerBlockEntity entity;
    protected final ContainerLevelAccess levelAccess;
    protected final ContainerData data;

    // Константы для слотов игрока (всегда одинаковы)
    protected static final int VANILLA_SLOT_COUNT = 36;
    protected static final int VANILLA_FIRST_SLOT_INDEX = 0;
    protected static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_SLOT_COUNT;

    protected DisassemblerMenu(MenuType<?> menuType, int containerID, Inventory inventory, BlockEntity entity, ContainerData data, int dataCount) {
        super(menuType, containerID);
        checkContainerSize(inventory, 4); // Минимальная проверка
        checkContainerDataCount(data, dataCount);

        this.entity = (DisassemblerBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(inventory.player.level(), entity.getBlockPos());
        this.data = data;
        drawPlayerInventory(inventory, 9, 107);
        drawPlayerHotbar(inventory, 9, 165);
        addDataSlots(data);
    }

    protected abstract Block getValidBlock();

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, getValidBlock());
    }

    protected void drawPlayerInventory(Inventory inventory, int x, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + i * 9 + 9, x + l * 18, y + i * 18));
            }
        }
    }

    protected void drawPlayerHotbar(Inventory inventory, int x, int y) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, x + i * 18, y));
        }
    }

    protected void drawGridOutput(IItemHandler handler, int x, int y, int startIndex) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = startIndex + (row * 3 + col);
                this.addSlot(new SlotItemHandler(handler, slotIndex, x + (col * 18), y + (row * 18)){
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false;
                    }
                });
            }
        }
    }


    public int getScaledProgress(int arrowSize) {
        int progress = getProgressValue();
        int maxProgress = getMaxProgressValue();
        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    protected int getProgressValue() { return this.data.get(0); }
    protected int getMaxProgressValue() { return this.data.get(1); }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index >= VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_SLOT_COUNT, true)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            if (!moveStackToMachine(sourceStack)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
    protected abstract boolean moveStackToMachine(ItemStack stack);
}