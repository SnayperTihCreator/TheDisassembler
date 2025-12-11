package snaypertihcreator.thedisassember.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDisassemberMod.MODID);

    public static final Map<String, RegistryObject<TeethItem>> TEETH_ITEMS = new HashMap<>();
    public static final Map<String, RegistryObject<SawItem>> SAW_ITEMS = new HashMap<>();

    static {
        for (MaterialType material : MaterialType.values()){
            String nameIngredient = material.getName();

            String nameTeeth = nameIngredient + "_teeth";
            String nameSaw = nameIngredient + "_saw";
            TierSaw tier = TierSaw.valueOf(material.getName().toUpperCase());

            RegistryObject<TeethItem> teethItem = ITEMS.register(nameTeeth, () -> new TeethItem(material));
            RegistryObject<SawItem> sawItem = ITEMS.register(nameSaw, () -> new SawItem(material, tier));

            TEETH_ITEMS.put(nameTeeth, teethItem);
            SAW_ITEMS.put(nameSaw, sawItem);
        }
    }
}
