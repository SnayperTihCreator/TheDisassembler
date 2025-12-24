package snaypertihcreator.thedisassembler.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;

import java.util.Objects;

public class Tier1DisassemblerMenu extends DisassemblerMenu {

    public Tier1DisassemblerMenu(int containerID, Inventory inventory, FriendlyByteBuf buf) {
        this(containerID, inventory, Objects.requireNonNull(inventory.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(3));
    }

    public Tier1DisassemblerMenu(int containerID, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TIER1_DISASSEMBLER_MENU.get(), containerID, inventory, entity, data, 3);

        this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 51, 43));
            drawGridOutput(handler, 110, 25, 1);
        });
    }

    @Override
    protected Block getValidBlock() {
        return ModBlocks.BASIC_DISASSEMBLER_BLOCK.get();
    }

    public boolean isRecipeValid() {
        return this.data.get(2) == 1;
    }

    @Override
    protected boolean moveStackToMachine(ItemStack stack) {
        int inputSlotIndex = TE_INVENTORY_FIRST_SLOT_INDEX;
        return moveItemStackTo(stack, inputSlotIndex, inputSlotIndex + 1, false);
    }
}