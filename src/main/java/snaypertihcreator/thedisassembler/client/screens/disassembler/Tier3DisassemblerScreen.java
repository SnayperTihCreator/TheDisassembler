package snaypertihcreator.thedisassembler.client.screens.disassembler;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.disassembler.Tier3DisassemblerMenu;

public class Tier3DisassemblerScreen extends DisassemblerScreen<Tier3DisassemblerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "textures/gui/disassembler_gui_tier3.png");

    public Tier3DisassemblerScreen(Tier3DisassemblerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 189;
        this.inventoryLabelY = 95;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected void renderBgExtras(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        int burnHeight = menu.getScaledFuelProgress(60);
        guiGraphics.blit(TEXTURE, x + 21, y + (80-burnHeight), 177, 74 - burnHeight, 18, burnHeight + 1);

        int progressWidth = menu.getScaledProgress(22);
        guiGraphics.blit(TEXTURE, x + 78, y + 43, 177, 0, progressWidth, 15);

    }
}
