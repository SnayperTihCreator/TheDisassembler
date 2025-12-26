package snaypertihcreator.thedisassembler.client.screens.distillation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.distillation.PrimitiveExtractorMenu;

public class Tier1ExtractorScreen extends ExtractorScreen<PrimitiveExtractorMenu>{
    // TODO сделать одну текстуру
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "textures/gui/disassembler_gui_tier1.png"); // TODO поменять текстуру на его

    public Tier1ExtractorScreen(PrimitiveExtractorMenu menu, Inventory inventory, Component component){
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
        /* TODO добавить шкалу (градусник). Нарисуйте вертикальную полоску. Здеся рисуйте заполнение этой полоски на основе getTemp(). */
        
        int progressWidth = menu.getScaledProgress(22);
        guiGraphics.blit(TEXTURE, x + 78, y + 43, 177, 0, progressWidth, 15);

    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        // TODO выводить оптимальную температуру
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY){
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        // TODO при наведении на "градусник" или текст всплывала подсказка
    }
}
