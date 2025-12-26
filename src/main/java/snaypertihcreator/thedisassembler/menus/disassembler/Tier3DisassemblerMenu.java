package snaypertihcreator.thedisassembler.menus.disassembler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.disassembler.HandSawItem;
import snaypertihcreator.thedisassembler.menus.ModMenuTypes;

import java.util.Objects;

public class Tier3DisassemblerMenu extends DisassemblerMenu {

    public Tier3DisassemblerMenu(int containerID, Inventory inventory, FriendlyByteBuf buf) {
        this(containerID, inventory, Objects.requireNonNull(inventory.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(4));
    }

    // Серверный конструктор
    public Tier3DisassemblerMenu(int containerID, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TIER3_DISASSEMBLER_MENU.get(), containerID, inventory, entity, data, 4);

        this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 51, 43));
            this.addSlot(new SlotItemHandler(handler, 1, 80, 61));
            drawGridOutput(handler, START_X_GRID, START_Y_GRID, 2);
        });
    }

    @Override
    protected Block getValidBlock() {
        return ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK.get();
    }

    //
    public int getScaledFuelProgress(int size) {
        int energyCurrent = this.data.get(2);
        int energyMax = this.data.get(3);
        if (energyMax == 0) energyMax = 60000;
        return energyMax > 0 ? energyCurrent * size / energyMax : 0;
    }

    @Override
    protected boolean moveStackToMachine(ItemStack stack) {
        int INPUT_SLOT = TE_INVENTORY_FIRST_SLOT_INDEX;
        int DISK_SLOT = TE_INVENTORY_FIRST_SLOT_INDEX + 1;

        if (stack.getItem() instanceof HandSawItem) {
            if (moveItemStackTo(stack, DISK_SLOT, DISK_SLOT + 1, false)) return true;
        }

        return moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false);
    }
}
