package snaypertihcreator.thedisassembler.menus.disassembler;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.disassembler.DisassemblerBlockEntity;
import snaypertihcreator.thedisassembler.menus.BaseMachineMenu;

public abstract class DisassemblerMenu extends BaseMachineMenu {
    public final DisassemblerBlockEntity entity;

    protected DisassemblerMenu(MenuType<?> menuType, int containerID, Inventory inventory, BlockEntity entity, ContainerData data, int dataCount) {
        super(menuType, containerID, inventory, entity, data, dataCount);
        this.entity = (DisassemblerBlockEntity) entity;
    }
}