package snaypertihcreator.thedisassembler.menus.distillation;

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
import snaypertihcreator.thedisassembler.items.distillation.DistillationKitItem;
import snaypertihcreator.thedisassembler.menus.ModMenuTypes;

import java.util.Objects;

public class PrimitiveExtractorMenu extends ExtractorMenu{
    public PrimitiveExtractorMenu(int containerID, Inventory inventory, FriendlyByteBuf buf) {
        this(containerID, inventory, Objects.requireNonNull(inventory.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(4));
    }

    public PrimitiveExtractorMenu(int containerID, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TIER1_EXTRACTOR_MENU.get(), containerID, inventory, entity, data, 4);

        entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 44, 35));
            this.addSlot(new SlotItemHandler(handler, 1, 62, 35));
            this.addSlot(new SlotItemHandler(handler, 2, 116, 35));
        });
    }

    @Override
    protected Block getValidBlock() {
        return ModBlocks.PRIMITIVE_EXTRACTOR_BLOCK.get();
    }

    @Override
    protected boolean moveStackToMachine(ItemStack stack) {
        final int INPUT_SLOT = TE_INVENTORY_FIRST_SLOT_INDEX;
        final int KIT_SLOT = TE_INVENTORY_FIRST_SLOT_INDEX+1;

        if (stack.getItem() instanceof DistillationKitItem){
            if (moveItemStackTo(stack, KIT_SLOT, KIT_SLOT+1, false)) return true;
        }

        return moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false);
    }
}
