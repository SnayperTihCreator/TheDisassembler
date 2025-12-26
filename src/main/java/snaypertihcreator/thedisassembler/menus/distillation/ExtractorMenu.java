package snaypertihcreator.thedisassembler.menus.distillation;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.distillation.ExtractorBlockEntity;
import snaypertihcreator.thedisassembler.menus.BaseMachineMenu;

public abstract class ExtractorMenu extends BaseMachineMenu {
    public final ExtractorBlockEntity entity;

    protected ExtractorMenu(MenuType<?> menuType, int containerID, Inventory inventory, BlockEntity entity, ContainerData data, int dataCount) {
        super(menuType, containerID, inventory, entity, data, dataCount);
        this.entity = (ExtractorBlockEntity) entity;
    }

    public float getCurrentTemp() {
        return this.data.get(2) / 10.0F;
    }

    public float getOptimalTemp() {return this.data.get(3);}

}