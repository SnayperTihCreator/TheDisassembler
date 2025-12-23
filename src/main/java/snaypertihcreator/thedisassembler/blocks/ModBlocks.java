package snaypertihcreator.thedisassembler.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.items.ModItems;

import java.util.function.Supplier;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDisassemblerMod.MODID);

    public static final RegistryObject<DisassemblerBlock> BASIC_BLOCK = registryBlock("basic_block", () -> new DisassemblerBlock(TierTheDisassembler.BASIC));
    public static final RegistryObject<DisassemblerBlock> ADVANCED_BLOCK = registryBlock("advanced_block", () -> new DisassemblerBlock(TierTheDisassembler.ADVANCED));
    public static final RegistryObject<DisassemblerBlock> PROGRESSIVE_BLOCK = registryBlock("progressive_block", () -> new DisassemblerBlock(TierTheDisassembler.PROGRESSIVE));

    // регистрация блока
    public static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block){
        RegistryObject<T> result = BLOCKS.register(name, block);
        registryItemBlock(name, result);
        return result;
    }

    // регистрация предмета самого блока
    public static <T extends Block> void registryItemBlock(String name, RegistryObject<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
