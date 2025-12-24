package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.items.disassembler.SawMaterial;

import java.util.Arrays;

/**
 * Провайдер для англиской локализации
 */
public class ModEnLangProvider extends LanguageProvider {
    public ModEnLangProvider(PackOutput output) {super(output, TheDisassemblerMod.MODID, "en_us");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_BLOCK.get(), "Basic Disassembler");
        add(ModBlocks.ADVANCED_BLOCK.get(), "Advanced Disassembler");
        add(ModBlocks.PROGRESSIVE_BLOCK.get(), "Progressive Disassembler");

        // TODO поправить переводы
        add("menujei.thedisassembler", "Disassembly");
        add("menu.thedisassembler.base_block", "Basic Disassembler");
        add("menu.thedisassembler.advanced_block", "Advanced Disassembler");
        add("menu.thedisassembler.progressive_block", "Progressive Disassembler");
        add("%s.disassembler_creative_tab".formatted(TheDisassemblerMod.MODID), "Disassembly");
        add("%s.distillation_creative_tab".formatted(TheDisassemblerMod.MODID), "Distillation");
        add("tooltip.thedisassembler.no_recipe", "Recipe not found");

        add("tooltip.thedisassembler.hold_shift", "Click on Shift for details");
        add("tooltip.thedisassembler.saw.description", "Saws that are used for automatic disassemblers");
        add("tooltip.thedisassembler.core.description", "Core for the saw");
        add("tooltip.thedisassembler.teeth.description", "Saw tooth");
        add("tooltip.thedisassembler.material.core", "Core Material: %s");
        add("tooltip.thedisassembler.material.teeth", "Teeth Material: %s");

        add("item.thedisassembler.saw_name", "%s Saw (%s teeth)");
        add("tooltip.thedisassembler.durability", "Durability: %s/%s");
        add("tooltip.thedisassembler.speedMod", "Processing Speed: %s%%");
        add("tooltip.thedisassembler.luckMod", "Luck Modifier: %s%%");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            String adj = material.getLang().getEnName();
            add("material.thedisassembler.%s.adj".formatted(material.getName()), material.getLang().getEnName());
            add("material.thedisassembler.%s.plural".formatted(material.getName()), material.getLang().getEnName());
            if (ModItems.SAW_ITEMS.containsKey(material)) add(ModItems.SAW_ITEMS.get(material).get(), "%s Saw".formatted(adj));
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), "%s Teeth".formatted(adj));
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), "%s Blade".formatted(adj));

        });
    }
}
