package snaypertihcreator.thedisassembler.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.IdMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class JSDisassembingRecipe extends  DisassemblingRecipe{
    private final Item inputItem;
    private final String scriptID;

    public JSDisassembingRecipe(ResourceLocation id, Item inputItem, String scriptID){
        super(id, Ingredient.of(inputItem), 1, List.of());
        this.inputItem = inputItem;
        this.scriptID = scriptID;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer container, Level world) {
        if (world.isClientSide) return false;
        ItemStack stack = container.getItem(0);
        return (stack.getItem() == inputItem) && (stack.getCount() >= getCountInput());
    }

    @Override
    public List<ItemStack> assembleOutputs(ItemStack input, Random random, float luckModifier) {
        return JSRecipeManager.getInstance().disassemble(input);
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        List<ItemStack> preview = JSRecipeManager.getInstance().disassemble(new ItemStack(inputItem));
        return preview.isEmpty() ? ItemStack.EMPTY : preview.get(0);
    }

    public static class Serializer implements RecipeSerializer<JSDisassembingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull JSDisassembingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            Item inputItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "input")).getItem();
            String scriptId = GsonHelper.getAsString(json, "script");
            return new JSDisassembingRecipe(id, inputItem, scriptId);
        }

        @Override
        public @Nullable JSDisassembingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
            ResourceLocation itemID = buf.readResourceLocation();
            Item inputItem = ForgeRegistries.ITEMS.getValue(itemID);
            String scriptId = buf.readUtf();
            return new JSDisassembingRecipe(id, inputItem, scriptId);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull JSDisassembingRecipe recipe) {
            buf.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(recipe.inputItem)));
            buf.writeUtf(recipe.scriptID);
        }
    }
}
