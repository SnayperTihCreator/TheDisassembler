package snaypertihcreator.thedisassember.menus;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;

// Регистратор меню
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TheDisassemberMod.MODID);

    public static final RegistryObject<MenuType<Tier1DisassemblerMenu>> TIER1_DISASSEMBLER_MENU = MENUS.register("disassembler_menu", () -> IForgeMenuType.create(Tier1DisassemblerMenu::new));
    public static final RegistryObject<MenuType<Tier2DisassemblerMenu>> TIER2_DISASSEMBLER_MENU = MENUS.register("disassembler2_menu", () -> IForgeMenuType.create(Tier2DisassemblerMenu::new));
}
