package snaypertihcreator.thedisassember.blocksEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.blocks.ModBlocks;

public class ModBlocksEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TheDisassemberMod.MODID);

    public static final RegistryObject<BlockEntityType<Tier1DisassemblerBlockEntity>> TIER1_DISASSEMBER_BE = BLOCKS_ENTITY.register(
            "disassember_be", () -> BlockEntityType.Builder.of(
                    Tier1DisassemblerBlockEntity::new,
                    ModBlocks.BASIC_BLOCK.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<Tier2DisassemblerBlockEntity>> TIER2_DISASSEMBER_BE = BLOCKS_ENTITY.register(
            "disassember2_be", () -> BlockEntityType.Builder.of(
                    Tier2DisassemblerBlockEntity::new,
                    ModBlocks.ADVANCED_BLOCK.get()
            ).build(null));
}
