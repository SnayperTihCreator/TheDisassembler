package snaypertihcreator.thedisassember.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.items.ModItems;

import java.util.function.Supplier;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDisassemberMod.MODID);

    public static final RegistryObject<DisassemberBlock> BASIC_BLOCK = registryBlock("basic_block", () -> new DisassemberBlock(TierTheDisassember.BASIC));

    public static <T extends Block> RegistryObject<T> registryBlock(String name, Supplier<T> block){
        RegistryObject<T> result = BLOCKS.register(name, block);
        registryItemBlock(name, result);
        return result;
    }

    public static <T extends Block> void registryItemBlock(String name, RegistryObject<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
