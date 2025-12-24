package snaypertihcreator.thedisassembler.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Регистратор предметов
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDisassemblerMod.MODID);

    public static final Map<SawMaterial, RegistryObject<SawTeethItem>> TEETH_ITEMS = new HashMap<>();
    public static final Map<SawMaterial, RegistryObject<SawCoreItem>> BLADE_ITEMS = new HashMap<>();
    public static final Map<SawMaterial, RegistryObject<HandSawItem>> SAW_ITEMS = new HashMap<>();

    public static final RegistryObject<BrewingSedimentItem> BREWING_SEDIMENT = ITEMS.register("brewing_sediment", BrewingSedimentItem::new);

    public static final RegistryObject<DistillationKitItem> GLASS_DISTILLATION = ITEMS.register("glass_distillation", () -> new DistillationKitItem(DistillationTier.GLASS));
    public static final RegistryObject<DistillationKitItem> REINFORCED_DISTILLATION = ITEMS.register("reinforced_distillation", () -> new DistillationKitItem(DistillationTier.REINFORCED));
    public static final RegistryObject<DistillationKitItem> DIAMOND_DISTILLATION = ITEMS.register("diamond_distillation", () -> new DistillationKitItem(DistillationTier.INDUSTRIAL));

    static {
        Arrays.stream(SawMaterial.values()).forEach(mat -> {
            TEETH_ITEMS.put(mat, ITEMS.register("%s_teeth".formatted(mat.getName()), () -> new SawTeethItem(mat)));
            BLADE_ITEMS.put(mat, ITEMS.register("%s_blade".formatted(mat.getName()), () -> new SawCoreItem(mat)));
            SAW_ITEMS.put(mat, ITEMS.register("%s_saw".formatted(mat.getName()), () -> new HandSawItem(mat, mat)));
        });
    }
}
