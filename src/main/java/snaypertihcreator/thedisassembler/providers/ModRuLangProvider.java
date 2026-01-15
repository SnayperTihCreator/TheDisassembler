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
        add(ModBlocks.BASIC_DISASSEMBLER_BLOCK.get(), "Базовый разборщик");
        add(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK.get(), "Улучшенный разборщик");
        add(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK.get(), "Продвинутый разборщик");

        add("tooltip.%s.basic_disassembler".formatted(TheDisassemblerMod.MODID), "Бонус разборки: нет");
        add("tooltip.%s.advanced_disassembler".formatted(TheDisassemblerMod.MODID), "Бонус разборки: бонус пилы");
        add("tooltip.%s.progressive_disassembler".formatted(TheDisassemblerMod.MODID), "Бонус разборки: бонус пилы + 5%");

        add("menu.thedisassembler.base_block", "Базовый разборщик");
        add("menu.thedisassembler.advanced_disassembler", "Улучшенный разборщик");
        add("menu.thedisassembler.progressive_disassembler", "Продвинутый разборщик");

        add("menu.%s.primitive_extractor".formatted(TheDisassemblerMod.MODID), "Примитивный Экстрактор");

        add("%s.disassembler_creative_tab".formatted(TheDisassemblerMod.MODID), "Разборка");
        add("%s.distillation_creative_tab".formatted(TheDisassemblerMod.MODID), "Дистилляция");
        add("tooltip.thedisassembler.no_recipe", "Не найден рецепт");
        add("tooltip.%s.empty_sediment".formatted(TheDisassemblerMod.MODID), "Пустой осадок");
        add("tooltip.%s.sediment_contains".formatted(TheDisassemblerMod.MODID), "Осадки");

        add("tooltip.thedisassembler.hold_shift", "Нажмите на Shift для подробностей");
        add("tooltip.thedisassembler.saw.description", "Пилы, используемые для автоматических разборщиков");
        add("tooltip.thedisassembler.material.core", "Основа: %s");
        add("tooltip.thedisassembler.material.teeth", "Зубья: %s");
        add("tooltip.thedisassembler.core.description", "Сердцевина для пилы");
        add("tooltip.thedisassembler.teeth.description", "Зуб для пилы");
        add("tooltip.thedisassembler.temperature", "Текущая температура %s °C");

        add("item.thedisassembler.saw_name", "%s Пила (%s зубья)");
        add("tooltip.thedisassembler.durability", "Прочность: %s/%s");
        add("tooltip.thedisassembler.speedMod", "Скорость распила: %s%%");
        add("tooltip.thedisassembler.luckMod", "Бонус к удаче: %s%%");
        add("tooltip.thedisassembler.max_efficiency", "Эффективность: %s");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            var name = material.getLang().getRuName();

            add(material.getComponentKey("adj"), name.feminine());
            add(material.getComponentKey("plural"), name.plural());
            add(material.getComponentKey("neutral"), name.neutral());
            if (ModItems.SAW_ITEMS.containsKey(material))add(ModItems.SAW_ITEMS.get(material).get(), "%s пила".formatted(name.feminine()));
            if (ModItems.CORE_ITEMS.containsKey(material)) add(ModItems.CORE_ITEMS.get(material).get(), "%s сердцевина".formatted(name.feminine()));
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), "%s зубья".formatted(name.plural()));
        });

        add(ModItems.BREWING_SEDIMENT.get(), "Варочный осадок");
        add(ModItems.GLASS_DISTILLATION.get(), "Стеклянный набор для дистилляции");
        add(ModItems.REINFORCED_DISTILLATION.get(), "Улучшенный набор для дистилляции");
        add(ModItems.DIAMOND_DISTILLATION.get(), "Продвинутый набор для дистилляции");
    }
}
