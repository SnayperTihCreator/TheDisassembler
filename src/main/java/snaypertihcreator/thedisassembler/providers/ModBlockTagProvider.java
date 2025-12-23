package snaypertihcreator.thedisassembler.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.ModBlocks;

import java.util.concurrent.CompletableFuture;

/**
 * Провайдер для регистрации тегов для блоков
 */
public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider,
                                   ExistingFileHelper helper){
        super(output, provider, TheDisassemblerMod.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.BASIC_BLOCK.get()).add(ModBlocks.ADVANCED_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.BASIC_BLOCK.get()).add(ModBlocks.ADVANCED_BLOCK.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.BASIC_BLOCK.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.ADVANCED_BLOCK.get());
    }
}
