package snaypertihcreator.thedisassembler.providers;

import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.*;
import snaypertihcreator.thedisassembler.items.disassembler.SawMaterial;
import snaypertihcreator.thedisassembler.recipes.ModRecipes;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Провайдер рецептов
 */
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output){
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {

        // Рецепт СБОРКИ пилы на верстаке
        SpecialRecipeBuilder.special(ModRecipes.SAW_ASSEMBLY.get())
                .save(consumer, "%s:saw_assembly_manual".formatted(TheDisassemblerMod.MODID));

        // Рецепт РАЗБОРА пилы
        consumer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {}

            @Override
            public @NotNull RecipeSerializer<?> getType() {
                return ModRecipes.SAW_DISASSEMBLING_SERIALIZER.get();
            }

            @Override
            public @NotNull ResourceLocation getId() {
                return ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "hand_saw_disassembling");
            }

            @Override
            public @Nullable JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public @Nullable ResourceLocation getAdvancementId() {
                return null;
            }
        });

        Arrays.stream(SawMaterial.values()).forEach(mat -> {
                    ItemLike teeth = ModItems.TEETH_ITEMS.get(mat).get();
                    ItemLike blade = ModItems.BLADE_ITEMS.get(mat).get();
                    ItemLike saw = ModItems.SAW_ITEMS.get(mat).get();

                    // 1. Зубья (2 шт)
                    SingleItemRecipeBuilder.stonecutting(mat.getRepairIngredient(),
                            RecipeCategory.MISC, teeth, 2)
                            .unlockedBy("has_mat_teeth", has(Items.STICK))
                            .save(consumer);

                    // 2. Лезвие (1 шт)
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, blade)
                            .pattern(" XX")
                            .pattern("XX ")
                            .define('X', mat.getRepairIngredient())
                            .unlockedBy("has_mat_blade", has(Items.STICK))
                            .save(consumer);

                    // 3. Стандартная пила (Основа + Зубья из одного материала)
                    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, saw)
                            .pattern(" T ")
                            .pattern("TBT")
                            .pattern(" T ")
                            .define('T', teeth)
                            .define('B', blade)
                            .unlockedBy("has_blade", has(blade))
                            .save(consumer);

        });

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BASIC_BLOCK.get())
                .pattern("ASB")
                .pattern("GVG")
                .pattern("GGG")
                .define('A', Items.IRON_AXE)
                .define('B', Items.IRON_PICKAXE)
                .define('S', ModItems.SAW_ITEMS.get(SawMaterial.IRON).get())
                .define('V', Items.CRAFTING_TABLE)
                .define('G', Items.SMOOTH_STONE)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ADVANCED_BLOCK.get())
                .pattern("ASB")
                .pattern("GPG")
                .pattern("GGG")
                .define('A', ModItems.SAW_ITEMS.get(SawMaterial.IRON).get())
                .define('B', Items.IRON_PICKAXE)
                .define('S', ModItems.SAW_ITEMS.get(SawMaterial.DIAMOND).get())
                .define('P', Items.FURNACE)
                .define('G', Items.SMOOTH_BASALT)
                .unlockedBy("has_basalt", has(Items.SMOOTH_BASALT))
                .save(consumer);
    }
}
