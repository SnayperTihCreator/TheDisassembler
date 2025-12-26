package snaypertihcreator.thedisassembler.menus;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.menus.disassembler.Tier1DisassemblerMenu;
import snaypertihcreator.thedisassembler.menus.disassembler.Tier2DisassemblerMenu;
import snaypertihcreator.thedisassembler.menus.disassembler.Tier3DisassemblerMenu;
import snaypertihcreator.thedisassembler.menus.distillation.PrimitiveExtractorMenu;

// Регистратор меню
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TheDisassemblerMod.MODID);

    public static final RegistryObject<MenuType<Tier1DisassemblerMenu>> TIER1_DISASSEMBLER_MENU = MENUS.register("disassembler_menu", () -> IForgeMenuType.create(Tier1DisassemblerMenu::new));
    public static final RegistryObject<MenuType<Tier2DisassemblerMenu>> TIER2_DISASSEMBLER_MENU = MENUS.register("disassembler2_menu", () -> IForgeMenuType.create(Tier2DisassemblerMenu::new));
    public static final RegistryObject<MenuType<Tier3DisassemblerMenu>> TIER3_DISASSEMBLER_MENU = MENUS.register("disassembler3_menu", () -> IForgeMenuType.create(Tier3DisassemblerMenu::new));

    public static final RegistryObject<MenuType<PrimitiveExtractorMenu>> TIER1_EXTRACTOR_MENU = MENUS.register("extractor_menu", () -> IForgeMenuType.create(PrimitiveExtractorMenu::new));
}
