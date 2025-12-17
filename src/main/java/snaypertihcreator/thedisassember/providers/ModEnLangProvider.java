package snaypertihcreator.thedisassember.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.items.SawMaterial;

import java.util.Arrays;


public class ModEnLangProvider extends LanguageProvider {
    public ModEnLangProvider(PackOutput output) {super(output, TheDisassemberMod.MODID, "en_us");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_BLOCK.get(), "Basic Disassembler");
        add(ModBlocks.ADVANCED_BLOCK.get(), "Advanced Disassembler");

        add("menujei.thedisassember", "Disassembly");
        add("menu.thedisassember.base_block", "Basic Disassembler");
        add("menu.thedisassember.advanced_block", "Advanced Disassembler");
        add("thedisassember.creative_tab", "The Disassembler");

        add("tooltip.thedisassember.saw.description", "Saws that are used for automatic disassemblers");
        add("tooltip.thedisassember.material.core", "The core");
        add("tooltip.thedisassember.material.teeth", "Teeth");

        add("tooltip.thedisassember.durability", "Durability");
        add("tooltip.thedisassember.hold_shift", "Click on Shift for details");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            String adj = material.getLang().getEnName();
            if (ModItems.SAW_ITEMS.containsKey(material)) add(ModItems.SAW_ITEMS.get(material).get(), adj+" Saw");
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), adj+" Teeth");
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), adj+" Blade");

        });
    }
}
