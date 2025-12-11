package snaypertihcreator.thedisassember.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.util.StringUtil;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.items.MaterialType;
import snaypertihcreator.thedisassember.items.ModItems;


public class ModEnLangProvider extends LanguageProvider {
    public ModEnLangProvider(PackOutput output) {super(output, TheDisassemberMod.MODID, "en_us");}

    @Override
    protected void addTranslations() {
        add(ModBlocks.BASIC_BLOCK.get(), "Basic Disassembler");

        for (MaterialType  material: MaterialType.values()){
            String adj = material.getLang().getEnName();
            String sawKey = material.getName()+"_saw";
            if (ModItems.SAW_ITEMS.containsKey(sawKey)) add(ModItems.SAW_ITEMS.get(sawKey).get(), adj+"Saw");

            String teethKey = material.getName()+"_teeth";
            if (ModItems.TEETH_ITEMS.containsKey(teethKey)) add(ModItems.TEETH_ITEMS.get(teethKey).get(), adj+"Teeth");
        }
    }
}
