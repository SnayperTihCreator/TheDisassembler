package snaypertihcreator.thedisassember;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.client.screens.Tier1DisassemblerScreen;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.menus.ModMenuTypes;

import java.util.Collection;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = TheDisassemberMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.TIER1_DISASSEMBLER_MENU.get(), Tier1DisassemblerScreen::new));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {

        registerColorsForItems(event,
                ModItems.SAW_ITEMS.values(),
                item -> item.getMaterial().getColor());

        registerColorsForItems(event,
                ModItems.TEETH_ITEMS.values(),
                item -> item.getMaterial().getColor());
    }

    private static <T extends Item> void registerColorsForItems(
            RegisterColorHandlersEvent.Item event,
            Collection<RegistryObject<T>> registryObjects,
            Function<T, Integer> colorProvider) {

        Item[] items = registryObjects.stream()
                .map(RegistryObject::get)
                .toArray(Item[]::new);

        event.register((stack, tintIndex) -> {
            @SuppressWarnings("unchecked")
            T item = (T) stack.getItem();
            return tintIndex == 0 ? colorProvider.apply(item) : -1;
        }, items);
    }
}
