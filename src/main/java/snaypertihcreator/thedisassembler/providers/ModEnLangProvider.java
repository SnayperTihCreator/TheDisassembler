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
 * Провайдер для англиской локализации
 */
public class ModEnLangProvider extends LanguageProvider {
    public ModEnLangProvider(PackOutput output) {super(output, TheDisassemblerMod.MODID, "en_us");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_DISASSEMBLER_BLOCK.get(), "Basic Disassembler");
        add(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK.get(), "Advanced Disassembler");
        add(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK.get(), "Progressive Disassembler");

        addBlockInfo(ModBlocks.BASIC_DISASSEMBLER_BLOCK, this::addToolTipTranslation, "Disassembly bonus: no");
        addBlockInfo(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK, this::addToolTipTranslation, "Disassembly bonus: saw blade bonus");
        addBlockInfo(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK, this::addToolTipTranslation, "Disassembly Bonus: saw blade bonus + 10%");

        addBlockInfo(ModBlocks.BASIC_DISASSEMBLER_BLOCK, this::addMenuTranslation, "Basic Disassembler");
        addBlockInfo(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK, this::addMenuTranslation, "Advanced Disassembler");
        addBlockInfo(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK, this::addMenuTranslation, "Progressive Disassembler");

        addBlockInfo(ModBlocks.PRIMITIVE_EXTRACTOR_BLOCK, this::addMenuTranslation, "Primitive Extractor");

        add("%s.disassembler_creative_tab".formatted(TheDisassemblerMod.MODID), "Disassembly");
        add("%s.distillation_creative_tab".formatted(TheDisassemblerMod.MODID), "Distillation");
        addToolTipTranslation("no_recipe", "Recipe not found");
        addToolTipTranslation("empty_sediment", "Empty Sediment");
        addToolTipTranslation("sediment_contains", "Sediments");

        addToolTipTranslation("hold_shift", "Click on Shift for details");
        addToolTipTranslation("saw.description", "Saws that are used for automatic disassemblers");
        addToolTipTranslation("core.description", "Core for the saw");
        addToolTipTranslation("teeth.description", "Saw tooth");
        addToolTipTranslation("material.core", "Core Material: %s");
        addToolTipTranslation("material.teeth", "Teeth Material: %s");
        addToolTipTranslation("temperature", "Current temperature %s °C");

        add("item.%s.saw_name".formatted(TheDisassemblerMod.MODID), "%s Saw (%s teeth)");
        addToolTipTranslation("durability", "Durability: %s/%s");
        addToolTipTranslation("speedMod", "Processing Speed: %s%%");
        addToolTipTranslation("luckMod", "Luck Modifier: %s%%");
        addToolTipTranslation("max_efficiency", "Efficiency: %s");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            String name = material.getLang().getEnName();
            add(material.getComponentKey("adj"), name);
            add(material.getComponentKey("plural"), name);
            add(material.getComponentKey("neutral"), name);
            if (ModItems.SAW_ITEMS.containsKey(material)) add(ModItems.SAW_ITEMS.get(material).get(), "%s Saw".formatted(name));
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), "%s Teeth".formatted(name));
            if (ModItems.CORE_ITEMS.containsKey(material)) add(ModItems.CORE_ITEMS.get(material).get(), "%s Blade".formatted(name));
        });

        add(ModItems.BREWING_SEDIMENT.get(), "Brewing Sediment");
        add(ModItems.GLASS_DISTILLATION.get(), "Glass distillation set");
        add(ModItems.REINFORCED_DISTILLATION.get(), "Improved distillation kit");
        add(ModItems.DIAMOND_DISTILLATION.get(), "Advanced distillation kit");
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
