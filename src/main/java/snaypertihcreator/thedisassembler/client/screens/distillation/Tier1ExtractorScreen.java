package snaypertihcreator.thedisassembler.client.screens.distillation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.distillation.Tier1ExtractorMenu;

import java.util.List;

public class Tier1ExtractorScreen extends ExtractorScreen<Tier1ExtractorMenu>{
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "textures/gui/extractor_gui_tier1.png");

    public Tier1ExtractorScreen(Tier1ExtractorMenu menu, Inventory inventory, Component component){
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

        float currentTemp = menu.getCurrentTemp()/700;
        int progressFire = Math.round(66*currentTemp);

        currentTemp *= 100;
        int x_uv = (currentTemp > 66) ? 205 : (currentTemp > 33) ? 191 : (currentTemp > 0) ? 177 : 0;

        if (x_uv > 0)
            guiGraphics.blit(TEXTURE, x + 19, y + (81-progressFire), x_uv, 94 - progressFire, 14, progressFire + 1);

        if (menu.isBurning())
            guiGraphics.blit(TEXTURE, x + 52, y + 62, 177, 15, 14, 14);

        int progressWidth = menu.getScaledProgress(22);
        guiGraphics.blit(TEXTURE, x + 78, y + 43, 177, 0, progressWidth, 15);

    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        String tempText = "%s °C".formatted(menu.getOptimalTemp());
        guiGraphics.drawString(this.font, tempText, 50, 28, 0x404040, false);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY){
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int relX = mouseX - this.leftPos;
        int relY = mouseY - this.topPos;

        if (relX >= 19 && relX <= 33 && relY >= 15 && relY <= 82) {
            Component tooltip = Component.translatable("tooltip.thedisassembler.temperature", menu.getCurrentTemp());
            guiGraphics.renderComponentTooltip(this.font, List.of(tooltip), mouseX, mouseY);
        }
    }
}
