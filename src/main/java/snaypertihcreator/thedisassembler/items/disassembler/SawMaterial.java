package snaypertihcreator.thedisassembler.items.disassembler;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import snaypertihcreator.thedisassembler.utils.ColorComponent;
import snaypertihcreator.thedisassembler.utils.MaterialLang;
import snaypertihcreator.thedisassembler.utils.MaterialTranslation;

import java.util.function.Supplier;

// материалы для пил, зубъев и серцевин
public enum SawMaterial {

    WOOD("wood", 1, 1, () -> Ingredient.of(ItemTags.PLANKS), MaterialTranslation.WOOD, ColorComponent.WOOD),
    STONE("stone", 2, 2, () -> Ingredient.of(Items.COBBLESTONE), MaterialTranslation.STONE, ColorComponent.STONE),
    IRON("iron", 3, 3, () -> Ingredient.of(Items.IRON_INGOT), MaterialTranslation.IRON, ColorComponent.IRON),
    GOLD("gold", 2, 4, () -> Ingredient.of(Items.GOLD_INGOT), MaterialTranslation.GOLD, ColorComponent.GOLD),
    DIAMOND("diamond", 4, 5, () -> Ingredient.of(Items.DIAMOND), MaterialTranslation.DIAMOND, ColorComponent.DIAMOND),
    NETHERITE("netherite", 5, 6, () -> Ingredient.of(Items.NETHERITE_INGOT), MaterialTranslation.NETHERITE, ColorComponent.NETHERITE);

    private final String name;
    private final int tierLevel;
    private final int speedLevel;
    private final Supplier<Ingredient> repairIngredient;
    private final MaterialTranslation lang;
    private final ColorComponent color;

    SawMaterial(String name, int tierLevel, int speedLevel, Supplier<Ingredient> repairIngredient, MaterialTranslation lang, ColorComponent color) {
        this.name = name;
        this.tierLevel = tierLevel;
        this.speedLevel = speedLevel;
        this.repairIngredient = repairIngredient;
        this.lang = lang;
        this.color = color;
    }

    // прочность материала
    public int getMaxUses() {return 32 * (int) Math.pow(2, this.tierLevel - 1);}
    // Чем выше speedLevel, тем быстрее идет прогресс
    public float getSpeedMultiplier() {return 1.0f + (this.speedLevel * 0.2f);}

    // Дерево (tier 1) = 0%, Незерит (tier 5) = 20%
    public float getLuckModifier() {return (this.tierLevel - 1) * 0.05f;}

    // имя материала
    public String getName() { return name; }
    // цвет материала
    public int getColor() { return color.getColor(); }
    // материал для починки
    public Ingredient getRepairIngredient() { return repairIngredient.get(); }
    // переводы
    public MaterialLang getLang() { return lang.getLang(); }
    public String getComponentKey(String suffix) {return lang.getTranslationKey(this.name, suffix);}

}