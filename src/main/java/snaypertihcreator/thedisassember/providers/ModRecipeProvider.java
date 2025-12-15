package snaypertihcreator.thedisassember.providers;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.items.MaterialType;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.items.SawItem;
import snaypertihcreator.thedisassember.items.TeethItem;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output){
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        for (MaterialType material: MaterialType.values()){
            String sawKey = material.getName() + "_saw";
            String teethKey = material.getName() + "_teeth";

            if (!ModItems.SAW_ITEMS.containsKey(sawKey) || !ModItems.TEETH_ITEMS.containsKey(teethKey)) {
                continue;
            }

            RegistryObject<SawItem> sawResult = ModItems.SAW_ITEMS.get(sawKey);
            RegistryObject<TeethItem> teethResult = ModItems.TEETH_ITEMS.get(teethKey);
            Ingredient ingredient = material.getIngredient();

            InventoryChangeTrigger.TriggerInstance criteria;
            if (material.isTag()) criteria = has(material.getTag());
            else criteria = has(material.getItem());

            SingleItemRecipeBuilder.stonecutting(
                    ingredient,
                    RecipeCategory.MISC,
                    teethResult.get(),
                    2
            )
                    .unlockedBy("has_" + material.getName(), criteria)
                    .save(consumer, TheDisassemberMod.MODID+":"+teethResult.getId().getPath()+"_stonecutting");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, sawResult.get())
                    .pattern(" A ")
                    .pattern("ABA")
                    .pattern(" A ")
                    .define('A', teethResult.get())
                    .define('B', ingredient)
                    .unlockedBy("has_teeth", has(teethResult.get()))
                    .save(consumer, TheDisassemberMod.MODID+":"+sawResult.getId().getPath()+"_shaped"
                    );
        }
    }
}
