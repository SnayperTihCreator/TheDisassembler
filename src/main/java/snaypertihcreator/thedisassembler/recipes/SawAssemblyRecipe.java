package snaypertihcreator.thedisassembler.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.items.HandSawItem;
import snaypertihcreator.thedisassembler.items.SawCoreItem;
import snaypertihcreator.thedisassembler.items.SawTeethItem;

/**
 * Класс для записи рецепта крафта без создания файла
 */
public class SawAssemblyRecipe extends CustomRecipe {

    public SawAssemblyRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world) {
        if (inv.getWidth() < 3 || inv.getHeight() < 3) {
            return false;
        }

        // 0 1 2
        // 3 4 5
        // 6 7 8

        ItemStack center = inv.getItem(4);
        ItemStack top = inv.getItem(1);
        ItemStack left = inv.getItem(3);
        ItemStack right = inv.getItem(5);
        ItemStack bottom = inv.getItem(7);

        if (!(center.getItem() instanceof SawCoreItem)) {
            return false;
        }

        if (!(top.getItem() instanceof SawTeethItem) ||
                !(left.getItem() instanceof SawTeethItem) ||
                !(right.getItem() instanceof SawTeethItem) ||
                !(bottom.getItem() instanceof SawTeethItem)) {
            return false;
        }
        if (!ItemStack.isSameItem(top, left) ||
                !ItemStack.isSameItem(top, right) ||
                !ItemStack.isSameItem(top, bottom)) {
            return false;
        }

        // 4. Проверяем, что углы пустые (0, 2, 6, 8)
        return inv.getItem(0).isEmpty() && inv.getItem(2).isEmpty() &&
                inv.getItem(6).isEmpty() && inv.getItem(8).isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess access) {
        ItemStack bladeStack = inv.getItem(4);
        ItemStack teethStack = inv.getItem(1);

        if (bladeStack.getItem() instanceof SawCoreItem blade &&
                teethStack.getItem() instanceof SawTeethItem teeth) {

            return HandSawItem.createSaw(blade.getMaterial(), teeth.getMaterial());
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.SAW_ASSEMBLY.get();
    }
}