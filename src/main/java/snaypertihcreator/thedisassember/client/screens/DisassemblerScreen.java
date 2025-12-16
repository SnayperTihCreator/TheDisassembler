package snaypertihcreator.thedisassember.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.menus.DisassemblerMenu;

public abstract class DisassemblerScreen<T extends DisassemblerMenu> extends AbstractContainerScreen<T> {

    public DisassemblerScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    protected abstract ResourceLocation getTexture();
    protected abstract void renderBgExtras(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY);

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getTexture());

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(getTexture(), x, y, 0, 0, imageWidth, imageHeight);

        renderBgExtras(guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}