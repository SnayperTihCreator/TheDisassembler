package snaypertihcreator.thedisassembler.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.items.disassembler.HandSawItem;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.items.disassembler.SawMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

// рецепт разборки пилы
public class SawDisassemblingRecipe extends DisassemblingRecipe{
    public SawDisassemblingRecipe(ResourceLocation id){
        super(id, Ingredient.EMPTY, 1, List.of());
    }

    @Override
    public boolean matches(@NotNull SimpleContainer container, Level world) {
        ItemStack stack = container.getItem(0);
        return !stack.isEmpty() && (stack.getItem() instanceof HandSawItem);
    }

    @Override
    public List<ItemStack> assembleOutputs(ItemStack input, Random random, float luckModifier) {
        List<ItemStack> list = new ArrayList<>();

        if (!(input.getItem() instanceof HandSawItem sawItem)) return list;

        SawMaterial coreMat = sawItem.getCore(input);
        SawMaterial teethMat = sawItem.getTeeth(input);

        if (!(ModItems.SAW_ITEMS.containsKey(coreMat))) return list;
        float finalChance = Math.min(1.0f, AUTO_RECIPE_CHANCE + luckModifier);

        if (random.nextFloat() <= finalChance)
            list.add(new ItemStack(ModItems.CORE_ITEMS.get(coreMat).get()));

        int teethCount = (int) IntStream.range(0, 4).filter(i -> random.nextFloat() <= finalChance).count();
        if (teethCount > 0) {
            ItemStack teeths = new ItemStack(ModItems.TEETH_ITEMS.get(teethMat).get(), teethCount);
            list.add(teeths);
        }
        return list;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAW_DISASSEMBLING_SERIALIZER.get();
    }

    @Override
    public List<Result> getResults() {
        return List.of(
                new Result(ModItems.CORE_ITEMS.get(SawMaterial.IRON).get().getDefaultInstance(), 0.75F),
                new Result(ModItems.TEETH_ITEMS.get(SawMaterial.IRON).get().getDefaultInstance(), 0.75F)
        );
    }

    public static class Serializer implements RecipeSerializer<SawDisassemblingRecipe> {
        @Override
        public @NotNull SawDisassemblingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            return new SawDisassemblingRecipe(id);
        }

        @Override
        public @Nullable SawDisassemblingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            return new SawDisassemblingRecipe(id);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SawDisassemblingRecipe recipe) {}
    }
}
