package snaypertihcreator.thedisassember.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.menus.Tier1DisassemblerMenu;
import snaypertihcreator.thedisassember.networking.ModMessages;
import snaypertihcreator.thedisassember.networking.PackSpined;


public class Tier1DisassemblerScreen extends AbstractContainerScreen<Tier1DisassemblerMenu> {
    private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "textures/gui/disassembler_gui.png");

    public Tier1DisassemblerScreen(Tier1DisassemblerMenu menu, Inventory inventory, Component component){
        super(menu, inventory, component);
        this.imageWidth = 177;
        this.imageHeight = 189;
        this.inventoryLabelY = 95;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(Button.builder(Component.literal("Разборка"),
                button -> {
                    this.setFocused(null);
                    ModMessages.sendToServer(new PackSpined());
                })
                .bounds(this.leftPos + 62, this.topPos + 84, 63, 12)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // Вычисляем центр экрана
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int progressWidth = menu.getScaledProgress(22);
        guiGraphics.blit(TEXTURE, x+78, y+43, 177, 0, progressWidth, 15);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
