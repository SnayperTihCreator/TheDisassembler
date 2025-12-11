package snaypertihcreator.thedisassember.items;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum MaterialType {

    WOOD("wood", 0x866526, ItemTags.PLANKS,
            new MaterialLang().en("Wood").ru("Деревянная", "Деревянные")),
    STONE("stone", 0x9a9a9a, ()->Items.COBBLESTONE,
            new MaterialLang().en("Stone").ru("Каменная", "Каменные")),
    IRON("iron", 0xffffff, ()->Items.IRON_INGOT,
            new MaterialLang().en("Iron").ru("Железная", "Железные")),
    GOLD("gold", 0xfdff76, ()->Items.GOLD_INGOT,
            new MaterialLang().en("Gold").ru("Золотая", "Золотые")),
    DIAMOND("diamond", 0x33ebcb, ()->Items.DIAMOND,
            new MaterialLang().en("Diamond").ru("Алмазная", "Алмазные")),
    NETHERITE("netherite", 0x867b86, ()->Items.NETHERITE_INGOT,
            new MaterialLang().en("Netherite").ru("Незеритовая", "Незеритовые"));


    private final String name;
    private final int color;

    private final Supplier<Ingredient> ingredient;
    private final TagKey<Item> tag;
    private final Supplier<Item> item;

    private final MaterialLang lang;

    MaterialType(String name, int color, Supplier<Item> item, MaterialLang lang){
        this.name = name;
        this.color = color;
        this.ingredient = () -> Ingredient.of(item.get());
        this.lang = lang;

        this.item = item;
        this.tag = null;
    }

    MaterialType(String name, int color, TagKey<Item> tag, MaterialLang lang){
        this.name = name;
        this.color = color;
        this.ingredient = () -> Ingredient.of(tag);
        this.lang = lang;

        this.item = null;
        this.tag = tag;
    }

    public String getName() {return name;}
    public int getColor() {return color;}
    public Ingredient getIngredient() {return ingredient.get();}
    public MaterialLang getLang() {return lang;}

    public boolean isTag() {return tag != null;}
    public TagKey<Item> getTag() {return tag;}
    public Item getItem() {return item.get();}
}
