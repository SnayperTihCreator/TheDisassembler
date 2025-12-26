package snaypertihcreator.thedisassembler.recipes;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class DistillationRecipeBuilder {
    private final Potion potion;
    private final Item result;
    private final float temperature;

    private DistillationRecipeBuilder(Potion potion, ItemLike result, float temperature) {
        this.potion = potion;
        this.result = result.asItem();
        this.temperature = temperature;
    }

    public static DistillationRecipeBuilder distil(Potion potion, ItemLike result, float temperature) {
        return new DistillationRecipeBuilder(potion, result, temperature);
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
                json.addProperty("potion", Objects.requireNonNull(ForgeRegistries.POTIONS.getKey(potion)).toString());
                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(result)).toString());
                json.add("result", resultJson);
                json.addProperty("temperature", temperature);
            }

            @Override
            public @NotNull ResourceLocation getId() { return id; }

            @Override
            public @NotNull RecipeSerializer<?> getType() { return ModRecipes.DISTILLATION_SERIALIZER.get(); }

            @Nullable
            @Override public JsonObject serializeAdvancement() { return null; }
            @Nullable @Override public ResourceLocation getAdvancementId() { return null; }
        });
    }
}
