package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.items.ModBlockItem;
import snaypertihcreator.thedisassembler.items.ModItems;

import java.util.function.Supplier;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDisassemblerMod.MODID);

    public static final RegistryObject<DisassemblerBlock> BASIC_DISASSEMBLER_BLOCK = registryBlock("basic_disassembler", () -> new DisassemblerBlock(TierTheDisassembler.BASIC), true);
    public static final RegistryObject<DisassemblerBlock> ADVANCED_DISASSEMBLER_BLOCK = registryBlock("advanced_disassembler", () -> new DisassemblerBlock(TierTheDisassembler.ADVANCED), true);
    public static final RegistryObject<DisassemblerBlock> PROGRESSIVE_DISASSEMBLER_BLOCK = registryBlock("progressive_disassembler", () -> new DisassemblerBlock(TierTheDisassembler.PROGRESSIVE), true);

    public static final RegistryObject<ExtractorBlock> PRIMITIVE_EXTRACTOR_BLOCK = registryBlock("primitive_extractor", () -> new ExtractorBlock(TierExtractor.PRIMITIVE));
    public static final RegistryObject<ExtractorBlock> COAL_EXTRACTOR_BLOCK = registryBlock("coal_extractor", () -> new CoalExtractorBlock(TierExtractor.COAL));

    // регистрация блока
    public static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block, boolean addToolTip){
        RegistryObject<T> result = BLOCKS.register(name, block);
        registryItemBlock(name, result, addToolTip);
        return result;
    }

    public static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block){
        RegistryObject<T> result = BLOCKS.register(name, block);
        registryItemBlock(name, result, false);
        return result;
    }

    // регистрация предмета самого блока
    public static <T extends Block> void registryItemBlock(String name, RegistryObject<T> block, boolean addToolTip){
        String tooltipKey = !addToolTip ? "": "tooltip.%s.%s".formatted(TheDisassemblerMod.MODID, name);
        ModItems.ITEMS.register(name, () -> new ModBlockItem(block.get(), tooltipKey));
    }
}
