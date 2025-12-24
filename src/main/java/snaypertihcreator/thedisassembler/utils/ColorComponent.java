package snaypertihcreator.thedisassembler.utils;

import net.minecraft.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public enum ColorComponent {

    WOOD(0x866526),
    STONE(0x9a9a9a),
    IRON(0xffffff),
    GOLD(0xfdff76),
    DIAMOND(0x33ebcb),
    NETHERITE(0x867b86);

    private final int color;
    ColorComponent(int color){
        this.color = color;
    }
    public int getColor() {
        return color;
    }

    private static final Map<Item, Integer> INGREDIENT_COLORS = Util.make(new HashMap<>(), map -> {
        map.put(Items.NETHER_WART, 0xFF5555); // Адский нарост (Красный)
        map.put(Items.SUGAR, 0xFFFFFF); // Сахар (Белый)
        map.put(Items.RABBIT_FOOT, 0xC69666);      // Лапка (Бежевый)
        map.put(Items.BLAZE_POWDER, 0xFFAA00);     // Огненный порошок (Оранжевый)
        map.put(Items.SPIDER_EYE, 0xA62626);       // Паучий глаз (Темно-красный)
        map.put(Items.FERMENTED_SPIDER_EYE, 0x3E5421); // Гнилой глаз
        map.put(Items.GHAST_TEAR, 0xEBF7F7);       // Слеза Гаста
        map.put(Items.MAGMA_CREAM, 0xD67016);      // Магма
        map.put(Items.GLISTERING_MELON_SLICE, 0xDDEE11); // Арбуз
        map.put(Items.GOLDEN_CARROT, 0xEEDD22);    // Морковь
        map.put(Items.PUFFERFISH, 0xDDBD33);       // Иглобрюх
        map.put(Items.TURTLE_HELMET, 0x44CC44);    // Черепаха
        map.put(Items.PHANTOM_MEMBRANE, 0xDDEEFF); // Мембрана
        map.put(Items.GUNPOWDER, 0x555555);        // Порох
        map.put(Items.GLOWSTONE_DUST, 0xFFCC00);   // Светопыль
        map.put(Items.REDSTONE, 0xFF0000);         // Редстоун
        map.put(Items.DRAGON_BREATH, 0xE88F96); // Дыхание дракона
    });

    public static int getIngredientColor(Item item) {
        return INGREDIENT_COLORS.getOrDefault(item, 0x808080);
    }

}
