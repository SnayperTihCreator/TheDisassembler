package snaypertihcreator.thedisassembler.creativetabs;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.items.ModItems;

// Регистратор креатив меню
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheDisassemblerMod.MODID);

    @SuppressWarnings("unused")
    public static final RegistryObject<CreativeModeTab> MOD_DISASSEMBLER_TAB = CREATIVE_MODE_TABS.register("%s_disas_tab".formatted(TheDisassemblerMod.MODID), ()->CreativeModeTab.builder()
            .title(Component.translatable("%s.disassembler_creative_tab".formatted(TheDisassemblerMod.MODID)))
            .icon(() -> new ItemStack(ModBlocks.BASIC_DISASSEMBLER_BLOCK.get().asItem()))
            .displayItems((parameters, output) -> {
                ModItems.SAW_ITEMS.values().forEach(item -> output.accept(item.get()));
                ModItems.CORE_ITEMS.values().forEach(item -> output.accept(item.get()));
                ModItems.TEETH_ITEMS.values().forEach(item -> output.accept(item.get()));

                output.accept(ModBlocks.BASIC_DISASSEMBLER_BLOCK.get());
                output.accept(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK.get());
                output.accept(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK.get());
            })
            .build());

    @SuppressWarnings("unused")
    public static final RegistryObject<CreativeModeTab> MOD_DISTILLATION_TAB = CREATIVE_MODE_TABS.register("%s_distil_tab".formatted(TheDisassemblerMod.MODID), () -> CreativeModeTab.builder()
            .title(Component.translatable("%s.distillation_creative_tab".formatted(TheDisassemblerMod.MODID)))
            .icon(() -> new ItemStack(ModItems.BREWING_SEDIMENT.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.BREWING_SEDIMENT.get());
                output.accept(ModItems.GLASS_DISTILLATION.get());

                output.accept(ModBlocks.PRIMITIVE_EXTRACTOR_BLOCK.get());
            })
            .build());
}
