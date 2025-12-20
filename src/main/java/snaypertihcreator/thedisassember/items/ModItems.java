package snaypertihcreator.thedisassember.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Регистратор предметов
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDisassemberMod.MODID);

    public static final Map<SawMaterial, RegistryObject<SawTeethItem>> TEETH_ITEMS = new HashMap<>();
    public static final Map<SawMaterial, RegistryObject<SawBladeItem>> BLADE_ITEMS = new HashMap<>();
    public static final Map<SawMaterial, RegistryObject<HandSawItem>> SAW_ITEMS = new HashMap<>();

    static {
        Arrays.stream(SawMaterial.values()).forEach(mat -> {
            TEETH_ITEMS.put(mat, ITEMS.register(mat.getName() + "_teeth", () -> new SawTeethItem(mat)));
            BLADE_ITEMS.put(mat, ITEMS.register(mat.getName() + "_blade", () -> new SawBladeItem(mat)));
            SAW_ITEMS.put(mat, ITEMS.register(mat.getName() + "_saw", () -> new HandSawItem(mat, mat)));
        });
    }
}
