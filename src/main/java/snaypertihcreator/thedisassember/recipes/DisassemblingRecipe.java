package snaypertihcreator.thedisassember.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Класс рецептов для самого разборщика
 */
public class DisassemblingRecipe implements Recipe<SimpleContainer> {
    protected static final float AUTO_RECIPE_CHANCE = 0.75F;

    private final ResourceLocation id;
    private final Ingredient input;
    private final int countInput;
    private final List<Result> results;

    public DisassemblingRecipe(ResourceLocation id, Ingredient input, int countInput, List<Result> results){
        this.id = id;
        this.input = input;
        this.countInput = countInput;
        this.results = results;
    }

    public Ingredient getInput() {
        return input;
    }

    public record Result(ItemStack stack, float chance) {
    }

    @Override
    public boolean matches(@NotNull SimpleContainer container, Level world) {
        if(world.isClientSide()) return false;
        ItemStack stack = container.getItem(0);
        return input.test(stack) && stack.getCount() >= countInput;
    }

    public List<Result> getResults() {
        return results;
    }

    public int getCountInput(){
        return countInput;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer container, @NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0).stack;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.DISASSEMBLING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.DISASSEMBLING_TYPE;
    }

    // метод создания рецепта из крафта
    public static DisassemblingRecipe fromCrafting(CraftingRecipe craftingRecipe, Level level) {
        ItemStack resultStack = craftingRecipe.getResultItem(level.registryAccess());

        Map<Item, Integer> ingredientCounts = new HashMap<>();
        craftingRecipe.getIngredients().forEach(ing -> {
            if (ing.isEmpty()) return;
            ItemStack[] matchingStacks = ing.getItems();
            if (matchingStacks.length > 0){
                Item item = matchingStacks[0].getItem();
                ingredientCounts.put(item, ingredientCounts.getOrDefault(item, 0)+1);
            }
        });

        List<Result> results = new ArrayList<>();
        ingredientCounts.forEach((item, count) -> {
            ItemStack stack = new ItemStack(item, count);
            results.add(new Result(stack, AUTO_RECIPE_CHANCE));
        });

        return new DisassemblingRecipe(
                craftingRecipe.getId(),
                Ingredient.of(resultStack),
                resultStack.getCount(),
                results
        );
    }

    // получаем итоговые предметы
    public List<ItemStack> assembleOutputs(ItemStack input, Random random, float luckModifier) {
        List<ItemStack> outputList = new ArrayList<>();
        results.forEach(result -> {
            ItemStack baseStack = result.stack;
            int maxCount = baseStack.getCount();
            float chance = Math.min(1.0F, result.chance + luckModifier);

            int finalCount = (int)IntStream.range(0, maxCount).filter(i -> random.nextFloat() <= chance).count();

            if (finalCount > 0) {
                ItemStack resultStack = baseStack.copyWithCount(finalCount);
                outputList.add(resultStack);
            }
        });

        return outputList;
    }

    public static class Serializer implements RecipeSerializer<DisassemblingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull DisassemblingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            int count = GsonHelper.getAsInt(json, "count", 1);

            List<Result> results = new ArrayList<>();
            JsonArray resultJson = GsonHelper.getAsJsonArray(json, "results");

            resultJson.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                ItemStack stack = ShapedRecipe.itemStackFromJson(object);
                float chance = GsonHelper.getAsFloat(object, "chance", AUTO_RECIPE_CHANCE);
                results.add(new Result(stack, chance));
            });

            return new DisassemblingRecipe(id, input, count, results);
        }

        @Override
        public @Nullable DisassemblingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            int count = buf.readInt();

            int size = buf.readInt();
            List<Result> results = new ArrayList<>(size);

            for (int i = 0; i < size; i++){
                ItemStack stack = buf.readItem();
                float chance = buf.readFloat();
                results.add(new Result(stack, chance));
            }

            return new DisassemblingRecipe(id, input, count, results);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, DisassemblingRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeInt(recipe.countInput);

            buf.writeInt(recipe.results.size());
            recipe.results.forEach(element -> {
                buf.writeItem(element.stack);
                buf.writeFloat(element.chance);
            });
        }
    }
}
