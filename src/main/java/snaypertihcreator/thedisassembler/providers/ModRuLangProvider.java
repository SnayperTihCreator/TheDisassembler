package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.items.disassembler.SawMaterial;

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
        add("%s.disassembler_creative_tab".formatted(TheDisassemblerMod.MODID), "Разборка");
        add("%s.distillation_creative_tab".formatted(TheDisassemblerMod.MODID), "Дистилляция");
        add("tooltip.thedisassembler.no_recipe", "Не найден рецепт");

        add("tooltip.thedisassembler.hold_shift", "Нажмите на Shift для подробностей");
        add("tooltip.thedisassembler.saw.description", "Пилы, используемые для автоматических разборщиков");
        add("tooltip.thedisassembler.material.core", "Основа: %s");
        add("tooltip.thedisassembler.material.teeth", "Зубья: %s");
        add("tooltip.thedisassembler.core.description", "Сердцевина для пилы");
        add("tooltip.thedisassembler.teeth.description", "Зуб для пилы");

        add("item.thedisassembler.saw_name", "%s Пила (%s зубья)");
        add("tooltip.thedisassembler.durability", "Прочность: %s/%s");
        add("tooltip.thedisassembler.speedMod", "Скорость распила: %s%%");
        add("tooltip.thedisassembler.luckMod", "Бонус к удаче: %s%%");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            add("material.thedisassembler.%s.adj".formatted(material.getName()), material.getLang().getRuName().feminine());
            add("material.thedisassembler.%s.plural".formatted(material.getName()), material.getLang().getRuName().plural());
            String adjSaw = material.getLang().getRuName().feminine();
            if (ModItems.SAW_ITEMS.containsKey(material))add(ModItems.SAW_ITEMS.get(material).get(), "%s пила".formatted(adjSaw));
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), "%s сердцевина".formatted(adjSaw));
            String adjTeeth = material.getLang().getRuName().plural();
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), "%s зубья".formatted(adjTeeth));
        });
    }
}
