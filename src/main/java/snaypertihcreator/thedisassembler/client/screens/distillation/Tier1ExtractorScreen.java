package snaypertihcreator.thedisassembler.client.screens.distillation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.distillation.PrimitiveExtractorMenu;

public class Tier1ExtractorScreen extends ExtractorScreen<PrimitiveExtractorMenu>{
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "textures/gui/extractor_gui_tier1.png"); //

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
        var currentTempure = menu.getCurrentTemp();
        if (currentTempure > 0) {
            var progressFire = (int)(66 * currentTempure) / 600;
            guiGraphics.blit(TEXTURE, x + 52, y + 44 + 12 - progressFire, 177, 66 - progressFire, 14, progressFire + 1);
        }

        guiGraphics.blit(TEXTURE, x + 19, y + 77, 177, 0, progressWidth, 15);

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
