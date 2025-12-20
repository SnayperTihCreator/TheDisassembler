package snaypertihcreator.thedisassember.items;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.function.Supplier;

// материалы для пил, зубъев и серцевин
public enum SawMaterial {

    WOOD("wood", 0x866526, 1, 1,// Уровень 1
            () -> Ingredient.of(ItemTags.PLANKS),
            new MaterialLang().en("Wood").ru("Деревянная", "Деревянные")),

    STONE("stone", 0x9a9a9a, 2, 2, // Уровень 2
            () -> Ingredient.of(Items.COBBLESTONE),
            new MaterialLang().en("Stone").ru("Каменная", "Каменные")),

    IRON("iron", 0xffffff, 3, 3,
            () -> Ingredient.of(Items.IRON_INGOT),
            new MaterialLang().en("Iron").ru("Железная", "Железные")),

    GOLD("gold", 0xfdff76, 2, 4, // Золото обычно слабее железа по прочности, но быстрее (тут уровень 2)
            () -> Ingredient.of(Items.GOLD_INGOT),
            new MaterialLang().en("Gold").ru("Золотая", "Золотые")),

    DIAMOND("diamond", 0x33ebcb, 4, 5,
            () -> Ingredient.of(Items.DIAMOND),
            new MaterialLang().en("Diamond").ru("Алмазная", "Алмазные")),

    NETHERITE("netherite", 0x867b86, 5, 6,
            () -> Ingredient.of(Items.NETHERITE_INGOT),
            new MaterialLang().en("Netherite").ru("Незеритовая", "Незеритовые"));

    private final String name;
    private final int color;
    private final int tierLevel;
    private final int speedLevel;
    private final Supplier<Ingredient> repairIngredient;
    private final MaterialLang lang;

    SawMaterial(String name, int color, int tierLevel, int speedLevel, Supplier<Ingredient> repairIngredient, MaterialLang lang) {
        this.name = name;
        this.color = color;
        this.tierLevel = tierLevel;
        this.speedLevel = speedLevel;
        this.repairIngredient = repairIngredient;
        this.lang = lang;
    }

    // прочность материала
    public int getMaxUses() {
        int baseDurability = 32;
        return baseDurability * (int) Math.pow(2, this.tierLevel - 1);
    }

    // имя материала
    public String getName() { return name; }
    // цвет материала
    public int getColor() { return color; }
    // скорость материала
    public int getSpeedLevel() {return speedLevel;}
    // материал для починки
    public Ingredient getRepairIngredient() { return repairIngredient.get(); }
    // переводы
    public MaterialLang getLang() { return lang; }

}