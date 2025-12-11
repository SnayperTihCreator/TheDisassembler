package snaypertihcreator.thedisassember.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.items.ModItems;

import java.util.Collection;
import java.util.Objects;

public class ModModelProvider extends ItemModelProvider {
    public ModModelProvider(PackOutput output, ExistingFileHelper helper){
        super(output, TheDisassemberMod.MODID, helper);
    }

    @Override
    protected void registerModels() {

        generateFlatItemModels(ModItems.TEETH_ITEMS.values(), "item/teeth");
        generateFlatItemModels(ModItems.SAW_ITEMS.values(), "item/saw");

    }

    private <T extends Item> void generateFlatItemModels(Collection<RegistryObject<T>> items, String texturePath) {
        ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, texturePath);

        for (RegistryObject<T> item : items) {
            String name = Objects.requireNonNull(item.getId()).getPath();
            withExistingParent(name, "item/generated").texture("layer0", baseTexture);
        }
    }
}
