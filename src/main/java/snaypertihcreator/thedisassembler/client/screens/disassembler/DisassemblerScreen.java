package snaypertihcreator.thedisassembler.client.screens.disassembler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import snaypertihcreator.thedisassembler.client.screens.BaseMachineScreen;
import snaypertihcreator.thedisassembler.menus.disassembler.DisassemblerMenu;

// рендер самого меню
public abstract class DisassemblerScreen<T extends DisassemblerMenu> extends BaseMachineScreen<T> {

    public DisassemblerScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }
}