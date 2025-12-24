package snaypertihcreator.thedisassembler.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.disassembler.HandSawItem;

import java.util.Objects;

public class Tier2DisassemblerMenu extends DisassemblerMenu {

    // Клиентский конструктор
    public Tier2DisassemblerMenu(int containerID, Inventory inventory, FriendlyByteBuf buf) {
        this(containerID, inventory, Objects.requireNonNull(inventory.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(4));
    }

    // Серверный конструктор
    public Tier2DisassemblerMenu(int containerID, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TIER2_DISASSEMBLER_MENU.get(), containerID, inventory, entity, data, 4);

        this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 51, 25));
            this.addSlot(new SlotItemHandler(handler, 1, 51, 61));
            this.addSlot(new SlotItemHandler(handler, 2, 80, 61));
            drawGridOutput(handler, 110, 25, 3);
        });
    }

    @Override
    protected Block getValidBlock() {
        return ModBlocks.ADVANCED_BLOCK.get();
    }

    @Override
    protected int getProgressValue() { return this.data.get(2); }
    @Override
    protected int getMaxProgressValue() { return this.data.get(3); }

    // Получаем если горит
    public boolean isBurning() {
        return this.data.get(0) > 0;
    }

    // изменяем огенек
    public int getScaledFuelProgress(int size) {
        int burnTime = this.data.get(0);
        int burnDuration = this.data.get(1);
        if (burnDuration == 0) burnDuration = 200;
        return burnTime > 0 ? burnTime * size / burnDuration : 0;
    }

    // метод для переноса предметов через shift
    @Override
    protected boolean moveStackToMachine(ItemStack stack) {
        int INPUT_SLOT = TE_INVENTORY_FIRST_SLOT_INDEX;
        int FUEL_SLOT = TE_INVENTORY_FIRST_SLOT_INDEX + 1;
        int DISK_SLOT = TE_INVENTORY_FIRST_SLOT_INDEX + 2;

        if (stack.getItem() instanceof HandSawItem) {
            if (moveItemStackTo(stack, DISK_SLOT, DISK_SLOT + 1, false)) return true;
        }

        if (ForgeHooks.getBurnTime(stack, null) > 0) {
            if (moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false)) return true;
        }

        return moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false);
    }
}