package snaypertihcreator.thedisassember.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.items.ModItems;

import java.util.Objects;

public class ModModelProvider extends ItemModelProvider {
    public ModModelProvider(PackOutput output, ExistingFileHelper helper){
        super(output, TheDisassemberMod.MODID, helper);
    }

    @Override
    protected void registerModels() {

        generateFlatItemModels(ModItems.TEETH_ITEMS.values(), "item/teeth");
        generateFlatItemModels(ModItems.BLADE_ITEMS.values(), "item/blade");

        ModItems.SAW_ITEMS.values().forEach(item -> {
            String name = Objects.requireNonNull(item.getId()).getPath();

            withExistingParent(name, "item/generated")
                    .texture("layer0", ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "item/saw_base"))
                    .texture("layer1", ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, "item/saw_overlay"));
        });
    }

    private void generateFlatItemModels(java.util.Collection<? extends RegistryObject<?>> items, String texturePath) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(TheDisassemberMod.MODID, texturePath);
        for (RegistryObject<?> item : items) {
            withExistingParent(Objects.requireNonNull(item.getId()).getPath(), "item/generated")
                    .texture("layer0", texture);
        }
    }
}
