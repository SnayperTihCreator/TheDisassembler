package snaypertihcreator.thedisassember.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.items.SawMaterial;

import java.util.Arrays;

public class ModRuLangProvider extends LanguageProvider {
    public ModRuLangProvider(PackOutput output) {super(output, TheDisassemberMod.MODID, "ru_ru");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_BLOCK.get(), "Базовый разборщик");
        add("menu." + TheDisassemberMod.MODID + ".base_block", "Разборка");
        add(TheDisassemberMod.MODID+".creative_tab", "Разбощик");

        Arrays.stream(SawMaterial.values()).forEach(material -> {

            String adjSaw = material.getLang().getRuName().feminine();
            if (ModItems.SAW_ITEMS.containsKey(material))add(ModItems.SAW_ITEMS.get(material).get(), adjSaw+ " пила");
            if (ModItems.BLADE_ITEMS.containsKey(material)) add(ModItems.BLADE_ITEMS.get(material).get(), adjSaw+" сердцевина");
            String adjTeeth = material.getLang().getRuName().plural();
            if (ModItems.TEETH_ITEMS.containsKey(material)) add(ModItems.TEETH_ITEMS.get(material).get(), adjTeeth + " зубья");
        });
    }
}
