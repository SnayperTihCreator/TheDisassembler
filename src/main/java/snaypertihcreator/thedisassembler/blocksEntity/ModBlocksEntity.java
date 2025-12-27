package snaypertihcreator.thedisassembler.blocksEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;
import snaypertihcreator.thedisassembler.blocksEntity.disassembler.DisassemblerBlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.disassembler.Tier1DisassemblerBlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.disassembler.Tier2DisassemblerBlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.disassembler.Tier3DisassemblerBlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.distillation.CoalExtractorBlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.distillation.ExtractorBlockEntity;
import snaypertihcreator.thedisassembler.blocksEntity.distillation.PrimitiveExtractorBlockEntity;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModBlocksEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TheDisassemblerMod.MODID);

    public static final RegistryObject<BlockEntityType<? extends DisassemblerBlockEntity>> TIER1_DISASSEMBLER_BE =
            registerBE("tier1_disassembler", Tier1DisassemblerBlockEntity::new, ModBlocks.BASIC_DISASSEMBLER_BLOCK);
    public static final RegistryObject<BlockEntityType<? extends DisassemblerBlockEntity>> TIER2_DISASSEMBLER_BE =
            registerBE("tier2_disassembler", Tier2DisassemblerBlockEntity::new, ModBlocks.ADVANCED_DISASSEMBLER_BLOCK);
    public static final RegistryObject<BlockEntityType<? extends DisassemblerBlockEntity>> TIER3_DISASSEMBLER_BE =
            registerBE("tier3_disassembler", Tier3DisassemblerBlockEntity::new, ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK);

    public static final RegistryObject<BlockEntityType<? extends ExtractorBlockEntity>> TIER1_DISTILLATION_BE =
            registerBE("tier1_distillation", PrimitiveExtractorBlockEntity::new, ModBlocks.PRIMITIVE_EXTRACTOR_BLOCK);
    public static final RegistryObject<BlockEntityType<? extends ExtractorBlockEntity>> TIER2_DISTILLATION_BE =
            registerBE("tier2_distillation", CoalExtractorBlockEntity::new);
    @SafeVarargs
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<? extends T>> registerBE(
            String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Supplier<? extends Block>... blocks) {
        return BLOCKS_ENTITY.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(Supplier::get).toArray(Block[]::new);
            return BlockEntityType.Builder.of(factory, validBlocks).build(null);
        });
    }
}
