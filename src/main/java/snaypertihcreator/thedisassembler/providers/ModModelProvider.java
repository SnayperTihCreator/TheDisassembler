package snaypertihcreator.thedisassembler.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.items.ModItems;

import java.util.Objects;

/**
 * Провайдер текстурок предметов
 */
public class ModModelProvider extends ItemModelProvider {
    public ModModelProvider(PackOutput output, ExistingFileHelper helper){
        super(output, TheDisassemblerMod.MODID, helper);
    }

    @Override
    protected void registerModels() {

        generateFlatItemModels(ModItems.TEETH_ITEMS.values(), "item/teeth");
        generateFlatItemModels(ModItems.CORE_ITEMS.values(), "item/blade");

        ModItems.SAW_ITEMS.values().forEach(item -> {
            String name = Objects.requireNonNull(item.getId()).getPath();

            withExistingParent(name, "item/generated")
                    .texture("layer0", ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "item/saw_base"))
                    .texture("layer1", ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "item/saw_overlay"));
        });
    }

    private void generateFlatItemModels(java.util.Collection<? extends RegistryObject<?>> items, String texturePath) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, texturePath);
        for (RegistryObject<?> item : items) {
            withExistingParent(Objects.requireNonNull(item.getId()).getPath(), "item/generated")
                    .texture("layer0", texture);
        }
    }
}
