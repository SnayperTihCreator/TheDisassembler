package snaypertihcreator.thedisassember.creativetabs;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.items.ModItems;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheDisassemberMod.MODID);

    public static final RegistryObject<CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register(TheDisassemberMod.MODID+"_tab", ()->CreativeModeTab.builder()
            .title(Component.translatable(TheDisassemberMod.MODID+".creative_tab"))
            .displayItems((parameters, output) -> {
                for (var item: ModItems.TEETH_ITEMS.values()) output.accept(item.get());
                for (var item: ModItems.SAW_ITEMS.values()) output.accept(item.get());
                output.accept(ModBlocks.BASIC_BLOCK.get());
            })
            .build());
}
