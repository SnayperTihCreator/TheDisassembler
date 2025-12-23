package snaypertihcreator.thedisassembler.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.recipes.DisassemblingRecipe;
import snaypertihcreator.thedisassembler.recipes.DisassemblyCache;
import snaypertihcreator.thedisassembler.recipes.ModRecipes;

import java.util.*;

// Сам плагин для JEI
@SuppressWarnings("unused")
@JeiPlugin
public class DisassemblerJeiPlugin implements IModPlugin {
    public static final RecipeType<DisassemblingRecipe> DISASSEMBLING_TYPE =
            new RecipeType<>(DisassemblerRecipeCategory.UID, DisassemblingRecipe.class);


    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "jei_plugin");
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
    }

    private void registerRecipesAssembler(IRecipeRegistration registration){
        List<DisassemblingRecipe> recipesToRegister = new ArrayList<>();
        Level world = Minecraft.getInstance().level;
        List<DisassemblingRecipe> customRecipes = new ArrayList<>();
        try {
            var manager = Objects.requireNonNull(world).getRecipeManager();
            customRecipes = manager.getAllRecipesFor(ModRecipes.DISASSEMBLING_TYPE);
            recipesToRegister.addAll(customRecipes);
        } catch (Exception ignored) {}

        Map<Item, DisassemblingRecipe> cachedRecipes = DisassemblyCache.getAllRecipes();

        if (cachedRecipes.isEmpty()) {
            registration.addRecipes(DISASSEMBLING_TYPE, recipesToRegister);
            return;
        }

        List<DisassemblingRecipe> finalCustomRecipes = customRecipes;
        cachedRecipes.forEach((item, autoRecipe) -> {
            ItemStack inputStack = new ItemStack(item);
            boolean hasCustom = finalCustomRecipes.stream()
                    .anyMatch(r -> r.getInput().test(inputStack));

            if (!hasCustom) {
                recipesToRegister.add(autoRecipe);
            }
        });

        registration.addRecipes(DISASSEMBLING_TYPE, recipesToRegister);
    }
}