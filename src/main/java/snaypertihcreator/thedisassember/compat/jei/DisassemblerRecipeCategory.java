package snaypertihcreator.thedisassember.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.recipes.DisassemblingRecipe;

import java.util.List;

public class DisassemblerRecipeCategory implements IRecipeCategory<DisassemblingRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "disassembling");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "textures/gui/disassembler_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public DisassemblerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 75, 30, 100, 60);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BASIC_BLOCK.get().asItem()));
    }

    @Override
    public @NotNull RecipeType<DisassemblingRecipe> getRecipeType() {
        return new RecipeType<>(UID, DisassemblingRecipe.class);
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("menu." + TheDisassemberMod.MODID + ".base_block");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DisassemblingRecipe recipe, IFocusGroup focuses) {
        // 1. ВХОД (Координаты относительно вырезанного background)
        // Если background вырезан с X=75, а слот был на X=80, то здесь X = 80 - 75 = 5.
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5)
                .addIngredients(recipe.getInput());

        // 2. ВЫХОДЫ
        List<DisassemblingRecipe.Result> results = recipe.getResults();

        // Рисуем сетку 3x3 (или как у вас в GUI)
        int startX = 5; // Смещение относительно фона
        int startY = 30; // Смещение относительно фона

        for (int i = 0; i < results.size(); i++) {
            if (i >= 9) break; // Максимум 9 слотов

            DisassemblingRecipe.Result result = results.get(i);
            int row = i / 3;
            int col = i % 3;

            builder.addSlot(RecipeIngredientRole.OUTPUT, startX + col * 18, startY + row * 18)
                    .addItemStack(result.stack())
                    .addTooltipCallback((recipeSlotView, tooltip) -> {
                        float chance = result.chance() * 100;
                        tooltip.add(Component.literal("§7Шанс: §6" + String.format("%.0f", chance) + "%"));
                    });
        }
    }
}