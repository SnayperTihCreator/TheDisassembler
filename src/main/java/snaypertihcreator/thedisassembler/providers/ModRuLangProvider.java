package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.items.SawMaterial;

import java.util.Arrays;

/**
 * Провайдер для русской локализации
 */
public class ModRuLangProvider extends LanguageProvider {
    public ModRuLangProvider(PackOutput output) {super(output, TheDisassemblerMod.MODID, "ru_ru");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_BLOCK.get(), "Базовый разборщик");
        add(ModBlocks.ADVANCED_BLOCK.get(), "Улучшенный разборщик");
        add(ModBlocks.PROGRESSIVE_BLOCK.get(), "Продвинутый разборщик");

        add("menujei.thedisassembler", "Разборка");
        add("menu.thedisassembler.base_block", "Базовый разборщик");
        add("menu.thedisassembler.advanced_block", "Улучшенный разборщик");
        add("menu.thedisassembler.progressive_block", "Продвинутый разборщик");
        add("thedisassembler.creative_tab", "Разборщики");
        add("tooltip.thedisassembler.no_recipe", "Не найден рецепт");

        add("tooltip.thedisassembler.hold_shift", "Нажмите на Shift для подробностей");
        add("tooltip.thedisassembler.saw.description", "Пилы, используемые для автоматических разборщиков");
        add("tooltip.thedisassembler.material.core", "Основа: %s");
        add("tooltip.thedisassembler.material.teeth", "Зубья: %s");
        add("item.thedisassembler.saw_name", "%s Пила (%s зубья)");
        add("tooltip.thedisassembler.durability", "Прочность: %s/%s");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            add("material.thedisassembler."+material.getName()+".adj", material.getLang().getRuName().feminine());
            add("material.thedisassembler."+material.getName()+".plural", material.getLang().getRuName().plural());
            String adjSaw = material.getLang().getRuName().feminine();
            if (ModItems.SAW_ITEMS.containsKey(material))add(ModItems.SAW_ITEMS.get(material).get(), adjSaw+ " пила");
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), adjSaw+" сердцевина");
            String adjTeeth = material.getLang().getRuName().plural();
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), adjTeeth + " зубья");
        });
    }
}
