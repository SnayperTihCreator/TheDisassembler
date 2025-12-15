package snaypertihcreator.thedisassember.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.TheDisassemberMod;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider,
                                   ExistingFileHelper helper){
        super(output, provider, TheDisassemberMod.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

    }
}
