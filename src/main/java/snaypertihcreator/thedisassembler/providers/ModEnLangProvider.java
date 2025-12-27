package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.items.disassembler.SawMaterial;
import snaypertihcreator.thedisassembler.recipes.DisassemblingRecipe;

import java.util.Arrays;

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

        add("tooltip.%s.basic_disassembler".formatted(TheDisassemblerMod.MODID), "Disassembly bonus: no");
        add("tooltip.%s.advanced_disassembler".formatted(TheDisassemblerMod.MODID), "Disassembly bonus: saw blade bonus");
        add("tooltip.%s.progressive_disassembler".formatted(TheDisassemblerMod.MODID), "Disassembly Bonus: saw blade bonus + 5%");

        // TODO поправить переводы
        add("menu.thedisassembler.base_block", "Basic Disassembler");
        add("menu.thedisassembler.advanced_disassembler", "Advanced Disassembler");
        add("menu.thedisassembler.progressive_disassembler", "Progressive Disassembler");
        add("%s.disassembler_creative_tab".formatted(TheDisassemblerMod.MODID), "Disassembly");
        add("%s.distillation_creative_tab".formatted(TheDisassemblerMod.MODID), "Distillation");
        add("tooltip.thedisassembler.no_recipe", "Recipe not found");
        add("tooltip.%s.empty_sediment".formatted(TheDisassemblerMod.MODID), "Empty Sediment");
        add("tooltip.%s.sediment_contains".formatted(TheDisassemblerMod.MODID), "Sediments");

        add("tooltip.thedisassembler.hold_shift", "Click on Shift for details");
        add("tooltip.thedisassembler.saw.description", "Saws that are used for automatic disassemblers");
        add("tooltip.thedisassembler.core.description", "Core for the saw");
        add("tooltip.thedisassembler.teeth.description", "Saw tooth");
        add("tooltip.thedisassembler.material.core", "Core Material: %s");
        add("tooltip.thedisassembler.material.teeth", "Teeth Material: %s");
        add("tooltip.thedisassembler.temperature", "Current temperature %s °C");

        add("item.thedisassembler.saw_name", "%s Saw (%s teeth)");
        add("tooltip.thedisassembler.durability", "Durability: %s/%s");
        add("tooltip.thedisassembler.speedMod", "Processing Speed: %s%%");
        add("tooltip.thedisassembler.luckMod", "Luck Modifier: %s%%");

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
        add(ModItems.GLASS_DISTILLATION.get(), "Glass Distillation Set");
    }
}
