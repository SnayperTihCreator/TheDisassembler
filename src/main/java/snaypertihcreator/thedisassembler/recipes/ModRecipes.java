package snaypertihcreator.thedisassembler.recipes;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;

/** Регистратор рецептов
 * */
public class ModRecipes {
    // Сам регистратор
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TheDisassemblerMod.MODID);

    // Тип сериализатора для кастомного предмета
    public static final RegistryObject<RecipeSerializer<DisassemblingRecipe>> DISASSEMBLING_SERIALIZER =
            SERIALIZERS.register("disassembling", () -> DisassemblingRecipe.Serializer.INSTANCE);

    // 2. Сериализатор для разбора пилы (Специальная логика Java) - ДОБАВЛЕНО
    public static final RegistryObject<RecipeSerializer<SawDisassemblingRecipe>> SAW_DISASSEMBLING_SERIALIZER =
            SERIALIZERS.register("saw_disassembling", SawDisassemblingRecipe.Serializer::new);

    // Тип сериализатора для крафта пил
    public static final RegistryObject<RecipeSerializer<SawAssemblyRecipe>> SAW_ASSEMBLY =
            SERIALIZERS.register("saw_assembly", () -> new SimpleCraftingRecipeSerializer<>(SawAssemblyRecipe::new));

    //Тип рецепта
    public static final RecipeType<DisassemblingRecipe> DISASSEMBLING_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return TheDisassemblerMod.MODID + ":disassembling";
        }
    };
}
