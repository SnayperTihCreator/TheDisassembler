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
        add("menu." + TheDisassemberMod.MODID + ".base_block", "Disassembly");
        add(TheDisassemberMod.MODID+".creative_tab", "The Disassembler");

        Arrays.stream(SawMaterial.values()).forEach(material -> {
            String adj = material.getLang().getEnName();
            if (ModItems.SAW_ITEMS.containsKey(material)) add(ModItems.SAW_ITEMS.get(material).get(), adj+" Saw");
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), adj+" Teeth");
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), adj+" Blade");

        });
    }
}
