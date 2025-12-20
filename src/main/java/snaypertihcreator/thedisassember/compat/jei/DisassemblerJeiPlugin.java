package snaypertihcreator.thedisassember.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.items.SawMaterial;
import snaypertihcreator.thedisassember.recipes.DisassemblingRecipe;
import snaypertihcreator.thedisassember.recipes.DisassemblyCache;
import snaypertihcreator.thedisassember.recipes.ModRecipes;

import java.util.*;

// Сам плагин для JEI
@SuppressWarnings("unused")
@JeiPlugin
public class DisassemblerJeiPlugin implements IModPlugin {
    public static final RecipeType<DisassemblingRecipe> DISASSEMBLING_TYPE =
            new RecipeType<>(DisassemblerRecipeCategory.UID, DisassemblingRecipe.class);


    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DisassemblerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.BASIC_BLOCK.get().asItem(), DISASSEMBLING_TYPE);
        registration.addRecipeCatalyst(ModBlocks.ADVANCED_BLOCK.get().asItem(), DISASSEMBLING_TYPE);
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        registerRecipesAssembler(registration);
        registerRecipesSawAssembly(registration);
    }

    private void registerRecipesAssembler(IRecipeRegistration registration){
        List<DisassemblingRecipe> allRecipes = new ArrayList<>();
        Level world = Minecraft.getInstance().level;

        try {
            var manager = Objects.requireNonNull(world).getRecipeManager();
            List<DisassemblingRecipe> customRecipes = manager.getAllRecipesFor(ModRecipes.DISASSEMBLING_TYPE);
            allRecipes.addAll(customRecipes);
        } catch (Exception ignored) {}

        Map<Item, CraftingRecipe> cachedRecipes = DisassemblyCache.getAllRecipes();

        if (cachedRecipes.isEmpty()) return;

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

        registration.addRecipes(DISASSEMBLING_TYPE, allRecipes);
    }

    private void registerRecipesSawAssembly(IRecipeRegistration registration){
        List<CraftingRecipe> sawRecipes = new ArrayList<>();

        Arrays.stream(SawMaterial.values()).forEach(mat -> {
            Ingredient bladeIng = Ingredient.of(ModItems.BLADE_ITEMS.get(mat).get());
            Ingredient teethIng = Ingredient.of(ModItems.TEETH_ITEMS.get(mat).get());
            ItemStack sawStack = new ItemStack(ModItems.SAW_ITEMS.get(mat).get());

            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);

            inputs.set(1, teethIng); // Верх
            inputs.set(3, teethIng); // Лево
            inputs.set(4, bladeIng); // Центр
            inputs.set(5, teethIng); // Право
            inputs.set(7, teethIng); // Низ

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "jei_saw_assembly_" + mat.name());

            ShapedRecipe recipe = new ShapedRecipe(
                    id,
                    "", // Группа
                    CraftingBookCategory.MISC,
                    3, 3,
                    inputs,
                    sawStack
            );


        });
        registration.addRecipes(RecipeTypes.CRAFTING, sawRecipes);
    }
}