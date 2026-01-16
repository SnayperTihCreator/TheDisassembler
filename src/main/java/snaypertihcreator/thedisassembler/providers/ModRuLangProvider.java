package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.items.disassembler.SawMaterial;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

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

        addBlockInfo(ModBlocks.BASIC_DISASSEMBLER_BLOCK, this::addToolTipTranslation, "Бонус разборки: нет");
        addBlockInfo(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK, this::addToolTipTranslation, "Бонус разборки: бонус пилы");
        addBlockInfo(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK, this::addToolTipTranslation, "Бонус разборки: бонус пилы + 10%");

        addBlockInfo(ModBlocks.BASIC_DISASSEMBLER_BLOCK, this::addMenuTranslation, "Базовый разборщик");
        addBlockInfo(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK, this::addMenuTranslation, "Улучшенный разборщик");
        addBlockInfo(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK, this::addMenuTranslation, "Продвинутый разборщик");

        addBlockInfo(ModBlocks.PRIMITIVE_EXTRACTOR_BLOCK, this::addMenuTranslation, "Примитивный Экстрактор");

        add("%s.disassembler_creative_tab".formatted(TheDisassemblerMod.MODID), "Разборка");
        add("%s.distillation_creative_tab".formatted(TheDisassemblerMod.MODID), "Дистилляция");
        addToolTipTranslation("no_recipe", "Не найден рецепт");
        addToolTipTranslation("empty_sediment", "Пустой осадок");
        addToolTipTranslation("sediment_contains", "Осадки");

        addToolTipTranslation("hold_shift", "Нажмите на Shift для подробностей");
        addToolTipTranslation("saw.description", "Пилы, используемые для автоматических разборщиков");
        addToolTipTranslation("material.core", "Основа: %s");
        addToolTipTranslation("material.teeth", "Зубья: %s");
        addToolTipTranslation("core.description", "Сердцевина для пилы");
        addToolTipTranslation("teeth.description", "Зуб для пилы");
        addToolTipTranslation("temperature", "Текущая температура %s °C");

        add("item.thedisassembler.saw_name", "%s Пила (%s зубья)");
        addToolTipTranslation("durability", "Прочность: %s/%s");
        addToolTipTranslation("speedMod", "Скорость распила: %s%%");
        addToolTipTranslation("luckMod", "Бонус к удаче: %s%%");
        addToolTipTranslation("max_efficiency", "Эффективность: %s");

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

    private void addToolTipTranslation(String tooltipKey, String tooltip){
        add("tooltip.%s.%s".formatted(TheDisassemblerMod.MODID, tooltipKey), tooltip);
    }

    private void addMenuTranslation(String tooltipKey, String tooltip){
        add("menu.%s.%s".formatted(TheDisassemblerMod.MODID, tooltipKey), tooltip);
    }

    private void addBlockInfo(RegistryObject<? extends Block> block, BiConsumer<String, String> adder, String tooltip){
        String blockID = Objects.requireNonNull(block.getId()).getPath();
        adder.accept(blockID, tooltip);
    }
}
