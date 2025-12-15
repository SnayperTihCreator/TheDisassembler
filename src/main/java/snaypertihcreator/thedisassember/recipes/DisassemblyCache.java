package snaypertihcreator.thedisassember.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import snaypertihcreator.thedisassember.TheDisassemberMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TheDisassemberMod.MODID)
public class DisassemblyCache {

    private static final Map<Item, CraftingRecipe> recipeMap = new HashMap<>();

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        recipeMap.clear();

        Level level = event.getServer().overworld();

        List<CraftingRecipe> craftingRecipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);

        craftingRecipes.forEach(recipe -> {
            ItemStack resultStack = recipe.getResultItem(level.registryAccess());
            if (resultStack.isEmpty()) return;

            Item resultItem = resultStack.getItem();
            if (!recipeMap.containsKey(resultItem)){
                recipeMap.put(resultItem, recipe);
            }
        });
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        recipeMap.clear();
    }

    public static CraftingRecipe getRecipe(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return recipeMap.get(stack.getItem());
    }

    public static boolean hasRecipe(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return recipeMap.containsKey(stack.getItem());
    }
}