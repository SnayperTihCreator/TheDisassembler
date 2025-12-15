package snaypertihcreator.thedisassember.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.tags.ModTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider,
                              CompletableFuture<TagLookup<Block>> tagLookup, @Nullable ExistingFileHelper helper){
        super(output, provider, tagLookup, TheDisassemberMod.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        ModItems.SAW_ITEMS.forEach((key, value) -> this.tag(ModTags.SAWS).add(value.get()));
        ModItems.TEETH_ITEMS.forEach((key, value) -> this.tag(ModTags.TEETHS).add(value.get()));
    }
}
