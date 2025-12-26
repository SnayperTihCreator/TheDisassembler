package snaypertihcreator.thedisassembler.client.screens.distillation;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import snaypertihcreator.thedisassembler.client.screens.BaseMachineScreen;
import snaypertihcreator.thedisassembler.menus.distillation.ExtractorMenu;

public abstract class ExtractorScreen<T extends ExtractorMenu> extends BaseMachineScreen<T> {

    public ExtractorScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}
