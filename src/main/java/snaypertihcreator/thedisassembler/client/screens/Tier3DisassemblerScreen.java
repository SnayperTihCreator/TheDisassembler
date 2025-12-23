package snaypertihcreator.thedisassembler.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.Tier3DisassemblerMenu;

public class Tier3DisassemblerScreen extends DisassemblerScreen<Tier3DisassemblerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "textures/gui/disassembler_gui_tier2.png"); //TODO поменять путь к текстуре

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
        int burnHeight = menu.getScaledFuelProgress(14);
        guiGraphics.blit(TEXTURE, x + 52, y + 44 + 12 - burnHeight, 177, 28 - burnHeight, 14, burnHeight + 1);

        int progressWidth = menu.getScaledProgress(22);
        guiGraphics.blit(TEXTURE, x + 78, y + 43, 177, 0, progressWidth, 15);

    }
}
