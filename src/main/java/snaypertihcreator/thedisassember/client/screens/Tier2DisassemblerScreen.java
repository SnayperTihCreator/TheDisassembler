package snaypertihcreator.thedisassember.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.menus.Tier2DisassemblerMenu;

public class Tier2DisassemblerScreen extends DisassemblerScreen<Tier2DisassemblerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "textures/gui/disassembler2_gui.png");

    public Tier2DisassemblerScreen(Tier2DisassemblerMenu menu, Inventory inventory, Component component) {
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
        // 1. Огонь (Топливо)
        if (menu.isBurning()) {
            int burnHeight = menu.getScaledFuelProgress(14);
            guiGraphics.blit(TEXTURE, x + 52, y + 44 + 12 - burnHeight, 177, 28 - burnHeight, 14, burnHeight + 1);
        }

        int progressWidth = menu.getScaledProgress(22);
        guiGraphics.blit(TEXTURE, x + 78, y + 43, 177, 0, progressWidth, 15);
    }
}