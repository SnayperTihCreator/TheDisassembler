package snaypertihcreator.thedisassembler.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.recipes.DisassemblingRecipe;

import java.util.List;

// Я ХУЙ ПОД ЧЕМ Я ЭТО ПИСАЛ. Это короче как рецепт рендерится в JEI
public class DisassemblerRecipeCategory implements IRecipeCategory<DisassemblingRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "disassembling");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "textures/gui/disassembler_jei.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public DisassemblerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 112, 60);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BASIC_BLOCK.get().asItem()));
        IDrawableStatic arrowStatic = helper.createDrawable(TEXTURE, 114, 0, 22, 15);
        this.arrow = helper.createAnimatedDrawable(arrowStatic, 100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public @NotNull RecipeType<DisassemblingRecipe> getRecipeType() {
        return new RecipeType<>(UID, DisassemblingRecipe.class);
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("menujei." + TheDisassemblerMod.MODID);
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public IDrawable getIcon() {return icon;}

    @Override
    public void draw(@NotNull DisassemblingRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        arrow.draw(guiGraphics, 28, 26);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DisassemblingRecipe recipe, @NotNull IFocusGroup focuses) {
        ItemStack stackInput = recipe.getInput().getItems()[0].copyWithCount(recipe.getCountInput());

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 26).addItemStack(stackInput);
        List<DisassemblingRecipe.Result> results = recipe.getResults();

        int startX = 60;
        int startY = 8;

        for (int i = 0; i < results.size(); i++) {
            if (i >= 9) return;
            DisassemblingRecipe.Result result = results.get(i);
            int row = i / 3;
            int col = i % 3;
            builder.addSlot(RecipeIngredientRole.OUTPUT, startX + col * 18, startY + row * 18)
                    .addItemStack(result.stack())
                    .addRichTooltipCallback((view, tooltip) -> {
                        float chance = result.chance() * 100;
                        int maxCount = result.stack().getCount();

                        if (chance < 100) {
                            tooltip.add(Component.literal("§7Кол-во: §f0-" + maxCount));
                            tooltip.add(Component.literal("§7Шанс: §6" + String.format("%.0f", chance) + "% §7(за шт.)"));
                        } else {
                            tooltip.add(Component.literal("§7Кол-во: §a" + maxCount));
                            tooltip.add(Component.literal("§7Шанс: §a100%"));
                        }
                    });
        }
    }
}