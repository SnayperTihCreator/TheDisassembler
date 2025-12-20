package snaypertihcreator.thedisassember.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import snaypertihcreator.thedisassember.ModCommonConfig;
import snaypertihcreator.thedisassember.TheDisassemberMod;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс для сбора рецептов верстака
 */
@Mod.EventBusSubscriber(modid = TheDisassemberMod.MODID)
public class DisassemblyCache {

    private static final Map<Item, CraftingRecipe> recipeMap = new HashMap<>(); //Хранить найдены рецепты

    // Запускается при запуске сервера
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        recipeMap.clear();

        @SuppressWarnings("resource") Level level = event.getServer().overworld();
        List<CraftingRecipe> craftingRecipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        List<? extends String> excludedConfigItems = ModCommonConfig.EXCLUDED_ITEMS.get();

        craftingRecipes.forEach(recipe -> {
            ItemStack resultStack = recipe.getResultItem(level.registryAccess());

            if (isExclude(resultStack, excludedConfigItems)) return;
            if (resultStack.isEmpty()) return;

            AtomicInteger inputCount = new AtomicInteger(0);
            Set<Item> uniqueIngredients = new HashSet<>();
            recipe.getIngredients().forEach(ingredient -> {
                if (ingredient.isEmpty()) return;
                inputCount.set(inputCount.get() + 1);
                ItemStack[] items = ingredient.getItems();
                if (items.length > 0) uniqueIngredients.add(items[0].getItem());
            });

            int outputCount = resultStack.getCount();

            if (inputCount.get() == 1 && outputCount == 9) return;
            if (inputCount.get() == 9 && outputCount == 1 && uniqueIngredients.size() == 1) return;

            Item resultItem = resultStack.getItem();
            if (!recipeMap.containsKey(resultItem)) recipeMap.put(resultItem, recipe);
        });
    }

    // Проверяет если предмет находится в списке исключения
    public static boolean isExclude(ItemStack stack, List<? extends String> configList){
        ResourceLocation itemID = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemID == null) return false;

        for (String configEntry : configList){
            if (configEntry.startsWith("#")){
                try {
                    String tagString = configEntry.substring(1);
                    ResourceLocation tagLoc = ResourceLocation.parse(tagString);
                    TagKey<Item> tag = ItemTags.create(tagLoc);

                    if (stack.is(tag)) return true;
                } catch (Exception ignored) {}
            } else {
                if (itemID.toString().equals(configEntry)) return true;
            }
        }
        return false;
    }

    // Запускается при остановке сервера
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        recipeMap.clear();
    }

    // гетер для доступа к рецепту
    public static @Nullable CraftingRecipe getRecipe(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return recipeMap.get(stack.getItem());
    }

    // гетер для получение всех найденных рецептов
    public static Map<Item, CraftingRecipe> getAllRecipes(){
        return recipeMap;
    }
}