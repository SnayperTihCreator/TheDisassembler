package snaypertihcreator.thedisassembler.blocksEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModBlocksEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TheDisassemblerMod.MODID);

    public static final RegistryObject<BlockEntityType<Tier1DisassemblerBlockEntity>> TIER1_DISASSEMBLER_BE =
            registerBE("tier1_disassembler", Tier1DisassemblerBlockEntity::new, ModBlocks.BASIC_BLOCK);

    public static final RegistryObject<BlockEntityType<Tier2DisassemblerBlockEntity>> TIER2_DISASSEMBLER_BE =
            registerBE("tier2_disassembler", Tier2DisassemblerBlockEntity::new, ModBlocks.ADVANCED_BLOCK);

    public static final RegistryObject<BlockEntityType<Tier3DisassemblerBlockEntity>> TIER3_DISASSEMBLER_BE =
            registerBE("tier3_disassembler", Tier3DisassemblerBlockEntity::new);

    @SafeVarargs
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBE(
            String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Supplier<? extends Block>... blocks) {
        return BLOCKS_ENTITY.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new);
            return BlockEntityType.Builder.of(factory, validBlocks).build(null);
        });
    }
}
