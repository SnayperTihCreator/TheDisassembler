package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;

import java.util.Set;

/**
 * Провайдер для регистрации таблицы лута
 */
public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables(){
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.BASIC_DISASSEMBLER_BLOCK.get());
        dropSelf(ModBlocks.ADVANCED_DISASSEMBLER_BLOCK.get());
        dropSelf(ModBlocks.PROGRESSIVE_DISASSEMBLER_BLOCK.get());

        dropSelf(ModBlocks.PRIMITIVE_EXTRACTOR_BLOCK.get());
        dropSelf(ModBlocks.COAL_EXTRACTOR_BLOCK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
