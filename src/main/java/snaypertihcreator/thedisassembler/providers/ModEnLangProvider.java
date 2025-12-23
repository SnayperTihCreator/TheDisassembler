package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.items.SawMaterial;

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

        add("menujei.thedisassembler", "Disassembly");
        add("menu.thedisassembler.base_block", "Basic Disassembler");
        add("menu.thedisassembler.advanced_block", "Advanced Disassembler");
        add("menu.thedisassembler.progressive_block", "Progressive Disassembler");
        add("thedisassembler.creative_tab", "The Disassembler");
        add("tooltip.thedisassembler.no_recipe", "Не найден рецепт");

        add("tooltip.thedisassembler.hold_shift", "Click on Shift for details");
        add("tooltip.thedisassembler.saw.description", "Saws that are used for automatic disassemblers");
        add("tooltip.thedisassembler.material.core", "Core Material: %s");
        add("tooltip.thedisassembler.material.teeth", "Teeth Material: %s");
        add("item.thedisassembler.saw_name", "%s Saw (%s teeth)");
        add("tooltip.thedisassembler.durability", "Durability: %s/%s");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            String adj = material.getLang().getEnName();
            add("material.thedisassembler."+material.getName()+".adj", material.getLang().getEnName());
            add("material.thedisassembler."+material.getName()+".plural", material.getLang().getEnName());
            if (ModItems.SAW_ITEMS.containsKey(material)) add(ModItems.SAW_ITEMS.get(material).get(), adj+" Saw");
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), adj+" Teeth");
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), adj+" Blade");

        });
    }
}
