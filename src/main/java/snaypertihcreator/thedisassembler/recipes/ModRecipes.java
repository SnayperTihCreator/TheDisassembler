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
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, TheDisassemblerMod.MODID);

    // Тип сериализатора для кастомного предмета
    public static final RegistryObject<RecipeSerializer<DisassemblingRecipe>> DISASSEMBLING_SERIALIZER =
            SERIALIZERS.register("disassembling", () -> DisassemblingRecipe.Serializer.INSTANCE);
    // Серилизатор дисциляции
    public static final RegistryObject<RecipeSerializer<DistillationRecipe>> DISTILLATION_SERIALIZER =
            SERIALIZERS.register("distillation", DistillationRecipe.Serializer::new);
    // Тип сериализатора для крафта пил
    public static final RegistryObject<RecipeSerializer<SawAssemblyRecipe>> SAW_ASSEMBLY =
            SERIALIZERS.register("saw_assembly", () -> new SimpleCraftingRecipeSerializer<>(SawAssemblyRecipe::new));

    //Тип рецепта
    public static final  RegistryObject<RecipeType<DisassemblingRecipe>> DISASSEMBLING_TYPE =
            TYPES.register("disassembling", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "%s:disassembling".formatted(TheDisassemblerMod.MODID);
                }
            });

    public static final RegistryObject<RecipeType<DistillationRecipe>> DISTILLATION_TYPE =
            TYPES.register("distillation", () -> new RecipeType<>(){
                @Override
                public String toString() {
                    return "%s:distillation".formatted(TheDisassemblerMod.MODID);
                }
            });
}
