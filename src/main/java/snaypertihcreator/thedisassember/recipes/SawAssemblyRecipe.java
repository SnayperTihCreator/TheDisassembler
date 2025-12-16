package snaypertihcreator.thedisassember.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.items.HandSawItem;
import snaypertihcreator.thedisassember.items.SawBladeItem;
import snaypertihcreator.thedisassember.items.SawTeethItem;

public class SawAssemblyRecipe extends CustomRecipe {

    // ВАЖНО: Конструктор должен принимать Category, чтобы совпадать с сериализатором
    public SawAssemblyRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
        boolean hasBlade = false;
        boolean hasTeeth = false;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof SawBladeItem) {
                    if (hasBlade) return false; // Нельзя два лезвия
                    hasBlade = true;
                } else if (stack.getItem() instanceof SawTeethItem) {
                    if (hasTeeth) return false; // Нельзя двое зубьев
                    hasTeeth = true;
                } else {
                    return false; // Лишние предметы запрещены
                }
            }
        }
        return hasBlade && hasTeeth;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess access) {
        SawBladeItem blade = null;
        SawTeethItem teeth = null;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof SawBladeItem b) blade = b;
            if (stack.getItem() instanceof SawTeethItem t) teeth = t;
        }

        if (blade != null && teeth != null) {
            // ВАЖНО: Убедитесь, что этот метод внутри HandSawItem работает и не возвращает null!
            return HandSawItem.createSaw(blade.getMaterial(), teeth.getMaterial());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAW_ASSEMBLY.get();
    }
}