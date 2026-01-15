package snaypertihcreator.thedisassembler.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import snaypertihcreator.thedisassembler.ModCommonConfig;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.items.disassembler.HandSawItem;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс для сбора рецептов верстака
 */
@Mod.EventBusSubscriber(modid = TheDisassemblerMod.MODID)
public class DisassemblyCache {

    // TODO почему-то не рабатает разборка фееверков

    private static final Map<Item, DisassemblingRecipe> recipeMap = new HashMap<>(); //Хранить найдены рецепты

    // Запускается при запуске сервера
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        recipeMap.clear();

        @SuppressWarnings("resource") Level level = event.getServer().overworld();
        List<CraftingRecipe> craftingRecipes = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING);
        List<? extends String> excludedConfigItems = ModCommonConfig.EXCLUDED_ITEMS.get();

        craftingRecipes.forEach(recipe -> {
            ItemStack resultStack = recipe.getResultItem(level.registryAccess());

            if(resultStack.is(Items.FIREWORK_ROCKET)){
                System.out.println("-----");
                System.out.println(resultStack);
                System.out.println(resultStack.getTag());
            }

            if (isExclude(resultStack, excludedConfigItems)) return;
            if (resultStack.isEmpty()) return;
            if (resultStack.getItem() instanceof HandSawItem) return;

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
            if (!recipeMap.containsKey(resultItem)) recipeMap.put(resultItem, DisassemblingRecipe.fromCrafting(recipe, level));
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

    // Очистка мапы при остановке сервера
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        recipeMap.clear();
    }

    // гетер для доступа к рецепту
    public static @Nullable DisassemblingRecipe getRecipe(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return recipeMap.get(stack.getItem());
    }

    // гетер для получение всех найденных рецептов
    public static Map<Item, DisassemblingRecipe> getAllRecipes(){
        return Collections.unmodifiableMap(recipeMap);
    }
}