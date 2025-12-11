package snaypertihcreator.thedisassember.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.items.MaterialType;
import snaypertihcreator.thedisassember.items.ModItems;

import java.util.HashMap;
import java.util.Map;

public class ModRuLangProvider extends LanguageProvider {
    public ModRuLangProvider(PackOutput output) {super(output, TheDisassemberMod.MODID, "ru_ru");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_BLOCK.get(), "Базовый разборщик");

        for (MaterialType material : MaterialType.values()) {
            String key = material.getName();

            String adjSaw = material.getLang().getRuName().feminine();
            String sawKey = key + "_saw";
            if (ModItems.SAW_ITEMS.containsKey(sawKey)) {
                add(ModItems.SAW_ITEMS.get(sawKey).get(), adjSaw+ " пила");
            }
            String adjTeeth = material.getLang().getRuName().plural();
            String teethKey = key + "_teeth";
            if (ModItems.TEETH_ITEMS.containsKey(teethKey)) {
                add(ModItems.TEETH_ITEMS.get(teethKey).get(), adjTeeth + " зубья");
            }
        }
    }
}
