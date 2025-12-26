package snaypertihcreator.thedisassembler.recipes;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SawDisassemblingRecipeBuilder {

    private SawDisassemblingRecipeBuilder() {}

    public static SawDisassemblingRecipeBuilder disassembling() {
        return new SawDisassemblingRecipeBuilder();
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
            }

            @Override
            public @NotNull ResourceLocation getId() {
                return id;
            }

            @Override
            public @NotNull RecipeSerializer<?> getType() {
                return ModRecipes.SAW_DISASSEMBLING_SERIALIZER.get();
            }

            @Nullable @Override public JsonObject serializeAdvancement() { return null; }
            @Nullable @Override public ResourceLocation getAdvancementId() { return null; }
        });
    }
}