package snaypertihcreator.thedisassembler.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.items.distillation.BrewingSedimentItem;
import snaypertihcreator.thedisassembler.items.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class DistillationRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Potion potionPredicate;
    private final ItemStack resultItem;
    private final float temperature;

    private static final float VARIANCE = 0.15f;

    public DistillationRecipe(ResourceLocation id, Potion potion, ItemStack result, float temperature){
        this.id = id;
        this.potionPredicate = potion;
        this.resultItem = result;
        this.temperature = temperature;
    }

    public ItemStack assembleSediment(ItemStack inputPotion, float kitEff, float burnFactor, Random random) {
        // 1. Собираем список всех сырых компонентов (100% эффективность)
        List<BrewingSedimentItem.SedimentContent> rawContents = gatherRawIngredients(inputPotion);

        float finalFactor = kitEff * burnFactor;

        // 2. Применяем математику и рандом
        List<BrewingSedimentItem.SedimentContent> finalContents = rawContents.stream()
                .map(c -> {
                    float randomMultiplier = 1.0f + (random.nextFloat() * 2.0f * VARIANCE) - VARIANCE;
                    float finalPercent = c.percentage() * finalFactor * randomMultiplier;
                    if (finalPercent < 0.05f) finalPercent = 0.0f;
                    finalPercent = Math.min(1.0f, Math.max(0.0f, finalPercent));
                    return new BrewingSedimentItem.SedimentContent(c.item(), finalPercent);
                })
                .filter(c -> c.percentage() > 0.0f)
                .toList();

        // 3. Создаем предмет
        ItemStack sediment = new ItemStack(ModItems.BREWING_SEDIMENT.get());
        BrewingSedimentItem.setColor(sediment, PotionUtils.getColor(inputPotion));
        BrewingSedimentItem.setContents(sediment, finalContents);

        return sediment;
    }

    private List<BrewingSedimentItem.SedimentContent> gatherRawIngredients(ItemStack stack) {
        List<BrewingSedimentItem.SedimentContent> list = new ArrayList<>();

        list.add(new BrewingSedimentItem.SedimentContent(resultItem.getItem(), 1.0f));

        Item bottle = stack.getItem();
        if (bottle == Items.SPLASH_POTION) list.add(new BrewingSedimentItem.SedimentContent(Items.GUNPOWDER, 1.0f));
        if (bottle == Items.LINGERING_POTION) list.add(new BrewingSedimentItem.SedimentContent(Items.DRAGON_BREATH, 1.0f));

        Potion potion = PotionUtils.getPotion(stack);
        String name = Objects.requireNonNull(ForgeRegistries.POTIONS.getKey(potion)).getPath();

        if (name.contains("long_")) list.add(new BrewingSedimentItem.SedimentContent(Items.REDSTONE, 1.0f));
        if (name.contains("strong_")) list.add(new BrewingSedimentItem.SedimentContent(Items.GLOWSTONE_DUST, 1.0f));

        if (resultItem.getItem() != Items.NETHER_WART) list.add(new BrewingSedimentItem.SedimentContent(Items.NETHER_WART, 0.5f));

        return list;
    }

    @Override
    public boolean matches(Container container, @NotNull Level level) {
        ItemStack input = container.getItem(0);
        Potion inputPotion = PotionUtils.getPotion(input);
        ResourceLocation inputId = ForgeRegistries.POTIONS.getKey(inputPotion);
        String baseName = Objects.requireNonNull(inputId).getPath().replace("long_", "").replace("strong_", "");
        ResourceLocation baseId = ResourceLocation.fromNamespaceAndPath(inputId.getNamespace(), baseName);

        return Objects.equals(ForgeRegistries.POTIONS.getKey(this.potionPredicate), baseId);
    }

    public float getTemperature() { return temperature; }
    @Override public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) { return resultItem.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) { return resultItem; }
    @Override public @NotNull ResourceLocation getId() { return id; }
    @Override public @NotNull RecipeSerializer<?> getSerializer() { return ModRecipes.DISTILLATION_SERIALIZER.get(); }
    @Override public @NotNull RecipeType<?> getType() { return ModRecipes.DISTILLATION_TYPE.get(); }

    public static class Serializer implements RecipeSerializer<DistillationRecipe> {

        @Override
        public @NotNull DistillationRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            String potionId = GsonHelper.getAsString(json, "potion");
            Potion potion = ForgeRegistries.POTIONS.getValue(ResourceLocation.parse(potionId));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            float temp = GsonHelper.getAsFloat(json, "temperature", 150.0f); // дефолт 150

            return new DistillationRecipe(recipeId, potion, output, temp);
        }

        @Override
        public @Nullable DistillationRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Potion potion = ForgeRegistries.POTIONS.getValue(buffer.readResourceLocation());
            ItemStack output = buffer.readItem();
            float temp = buffer.readFloat();
            return new DistillationRecipe(recipeId, potion, output, temp);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DistillationRecipe recipe) {
            buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.POTIONS.getKey(recipe.potionPredicate)));
            buffer.writeItem(recipe.resultItem);
            buffer.writeFloat(recipe.temperature);
        }
    }
}
