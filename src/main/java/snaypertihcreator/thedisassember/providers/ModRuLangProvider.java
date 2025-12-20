package snaypertihcreator.thedisassember.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.items.SawMaterial;

import java.util.Arrays;

public class ModRuLangProvider extends LanguageProvider {
    public ModRuLangProvider(PackOutput output) {super(output, TheDisassemberMod.MODID, "ru_ru");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_BLOCK.get(), "Базовый разборщик");
        add(ModBlocks.ADVANCED_BLOCK.get(), "Улучшенный разборщик");

        add("menujei.thedisassember", "Разборка");
        add("menu.thedisassember.base_block", "Базовый разборщик");
        add("menu.thedisassember.advanced_block", "Улучшенный разборщик");
        add("thedisassember.creative_tab", "Разборка");
        add("tooltip.thedisassember.no_recipe", "Не найден рецепт");

        add("tooltip.thedisassember.hold_shift", "Нажмите на Shift для подробностей");
        add("tooltip.thedisassember.saw.description", "Пилы, используемые для автоматических разборщиков");
        add("tooltip.thedisassember.material.core", "Основа: %s");
        add("tooltip.thedisassember.material.teeth", "Зубья: %s");
        add("item.thedisassember.saw_name", "%s Пила (%s зубья)");
        add("tooltip.thedisassember.durability", "Прочность: %s/%s");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            add("material.thedisassember."+material.getName()+".adj", material.getLang().getRuName().feminine());
            add("material.thedisassember."+material.getName()+".plural", material.getLang().getRuName().plural());
            String adjSaw = material.getLang().getRuName().feminine();
            if (ModItems.SAW_ITEMS.containsKey(material))add(ModItems.SAW_ITEMS.get(material).get(), adjSaw+ " пила");
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), adjSaw+" сердцевина");
            String adjTeeth = material.getLang().getRuName().plural();
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), adjTeeth + " зубья");
        });
    }
}
