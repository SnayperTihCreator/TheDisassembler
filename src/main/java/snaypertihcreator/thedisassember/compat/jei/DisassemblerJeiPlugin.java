package snaypertihcreator.thedisassember.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.recipes.DisassemblingRecipe;
import snaypertihcreator.thedisassember.recipes.DisassemblyCache;
import snaypertihcreator.thedisassember.recipes.ModRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@JeiPlugin
public class DisassemblerJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DisassemblerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        RecipeType<DisassemblingRecipe> type = new RecipeType<>(DisassemblerRecipeCategory.UID, DisassemblingRecipe.class);
        List<DisassemblingRecipe> allRecipes = new ArrayList<>();
        Level world = Minecraft.getInstance().level;

        try {
            var manager = Objects.requireNonNull(world).getRecipeManager();
            List<DisassemblingRecipe> customRecipes = manager.getAllRecipesFor(ModRecipes.DISASSEMBLING_TYPE);
            allRecipes.addAll(customRecipes);
        } catch (Exception ignored) {}

        Map<Item, CraftingRecipe> cachedRecipes = DisassemblyCache.getAllRecipes();

        if (cachedRecipes == null) return;

        cachedRecipes.forEach((key, recipe) -> {
            ItemStack resultItem = new ItemStack(key);



            boolean hasCustom = allRecipes.stream().anyMatch(r -> r.getInput().test(resultItem));
            if (hasCustom) return;

            int countInput = recipe.getResultItem(Objects.requireNonNull(world).registryAccess()).getCount();
            Ingredient input = Ingredient.of(resultItem);
            List<DisassemblingRecipe.Result> results = new ArrayList<>();
            recipe.getIngredients().forEach(ingredient -> {
                if (ingredient.isEmpty()) return;
                ItemStack[] stacks = ingredient.getItems();
                if (stacks.length > 0) {
                    ItemStack itemToAdd = stacks[0].copy();
                    boolean found = false;
                    for (DisassemblingRecipe.Result existingResult : results) {
                        if (ItemStack.isSameItemSameTags(existingResult.stack(), itemToAdd)) {
                            existingResult.stack().grow(1);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        results.add(new DisassemblingRecipe.Result(itemToAdd, 0.75f));
                    }
                }
            });

            ResourceLocation fakeId = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "auto_generated_" + resultItem.getDescriptionId());
            DisassemblingRecipe fakeRecipe = new DisassemblingRecipe(fakeId, input, countInput, results);
            allRecipes.add(fakeRecipe);
        });

        registration.addRecipes(type, allRecipes);
    }
}