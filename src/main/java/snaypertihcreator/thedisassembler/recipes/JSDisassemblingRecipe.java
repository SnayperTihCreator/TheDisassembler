package snaypertihcreator.thedisassembler.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Random;

/**
 * Специальный тип рецепта, который делегирует логику разборки в JavaScript.
 */
public class JSDisassemblingRecipe extends DisassemblingRecipe {
    private final Item targetItem;

    public JSDisassemblingRecipe(ResourceLocation id, Item item) {
        super(id,
                Ingredient.of(item),
                JSRecipeManager.getInstance().getMinInput(item),
                List.of());
        this.targetItem = item;
    }

    @Override
    public List<ItemStack> assembleOutputs(ItemStack input, Random random, float luckModifier) {
        return JSRecipeManager.getInstance().disassemble(input, random, luckModifier);
    }

    @Override
    public String toString() {
        return "JSDisassemblingRecipe(target=" + targetItem + ")";
    }
}