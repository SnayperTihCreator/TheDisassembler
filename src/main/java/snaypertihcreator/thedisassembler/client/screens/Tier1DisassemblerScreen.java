package snaypertihcreator.thedisassembler.client.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.Tier1DisassemblerMenu;
import snaypertihcreator.thedisassembler.networking.ModMessages;
import snaypertihcreator.thedisassembler.networking.PackSpined;

public class Tier1DisassemblerScreen extends DisassemblerScreen<Tier1DisassemblerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "textures/gui/disassembler_gui_tier1.png");

    public Tier1DisassemblerScreen(Tier1DisassemblerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 177;
        this.imageHeight = 189;
        this.inventoryLabelY = 95;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.literal("Разборка"),
                        _ -> {
                            this.setFocused(null);
                            ModMessages.sendToServer(new PackSpined());
                        })
                .bounds(this.leftPos + 62, this.topPos + 84, 63, 12)
                .build());
    }

    @Override
    protected void renderBgExtras(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        int progressWidth = menu.getScaledProgress(22);
        guiGraphics.blit(TEXTURE, x + 78, y + 43, 177, 0, progressWidth, 15);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (this.menu.getSlot(36).hasItem()) {
            if (menu.isRecipeValid()) {
                guiGraphics.drawString(this.font, "✔", x + 30, y + 46, 0x00FF00, true);
            } else {
                guiGraphics.drawString(this.font, "✖", x + 30, y + 46, 0xFF0000, true);

                // Тултип ошибки, если навели на стрелку
                if (isHovering(78, 43, 22, 15, mouseX, mouseY)) {
                    guiGraphics.renderTooltip(this.font, Component.translatable("tooltip.thedisassembler.no_recipe").withStyle(ChatFormatting.RED), mouseX, mouseY);
                }
            }
        }
    }
}