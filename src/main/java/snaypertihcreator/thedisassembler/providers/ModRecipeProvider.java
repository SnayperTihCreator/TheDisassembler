package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.*;
import snaypertihcreator.thedisassembler.items.disassembler.SawMaterial;
import snaypertihcreator.thedisassembler.recipes.DistillationRecipeBuilder;
import snaypertihcreator.thedisassembler.recipes.ModRecipes;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Провайдер рецептов
 */

// TODO проверить наличие всех крафтов для нужных предметов (желательно создать TASKS_RECIPE.md выдвинуть туда рецепт предметов)
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output){
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {

        // Рецепт СБОРКИ пилы на верстаке
        SpecialRecipeBuilder.special(ModRecipes.SAW_ASSEMBLY.get())
                .save(consumer, "%s:saw_assembly_manual".formatted(TheDisassemblerMod.MODID));

        Arrays.stream(SawMaterial.values()).forEach(mat -> {
                    ItemLike teeth = ModItems.TEETH_ITEMS.get(mat).get();
                    ItemLike blade = ModItems.CORE_ITEMS.get(mat).get();
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BASIC_DISASSEMBLER_BLOCK.get())
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ADVANCED_DISASSEMBLER_BLOCK.get())
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


        addDistil(consumer, Potions.NIGHT_VISION, Items.GOLDEN_CARROT, 120f);
        addDistil(consumer, Potions.INVISIBILITY, Items.FERMENTED_SPIDER_EYE, 140f);
        addDistil(consumer, Potions.LEAPING, Items.RABBIT_FOOT, 130f);
        addDistil(consumer, Potions.FIRE_RESISTANCE, Items.MAGMA_CREAM, 250f);
        addDistil(consumer, Potions.SWIFTNESS, Items.SUGAR, 100f);
        addDistil(consumer, Potions.WATER_BREATHING, Items.PUFFERFISH, 110f);
        addDistil(consumer, Potions.HEALING, Items.GLISTERING_MELON_SLICE, 160f);
        addDistil(consumer, Potions.HARMING, Items.FERMENTED_SPIDER_EYE, 180f);
        addDistil(consumer, Potions.POISON, Items.SPIDER_EYE, 150f);
        addDistil(consumer, Potions.REGENERATION, Items.GHAST_TEAR, 300f);
        addDistil(consumer, Potions.STRENGTH, Items.BLAZE_POWDER, 350f);
        addDistil(consumer, Potions.WEAKNESS, Items.FERMENTED_SPIDER_EYE, 120f);
        addDistil(consumer, Potions.SLOW_FALLING, Items.PHANTOM_MEMBRANE, 160f);
        addDistil(consumer, Potions.TURTLE_MASTER, Items.TURTLE_HELMET, 400f);
    }

    private void addDistil(Consumer<FinishedRecipe> consumer, Potion potion, ItemLike result, float temp) {
        String name = Objects.requireNonNull(ForgeRegistries.POTIONS.getKey(potion)).getPath();
        DistillationRecipeBuilder.distil(potion, result, temp)
                .save(consumer, ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "distillation/" + name));
    }
}
