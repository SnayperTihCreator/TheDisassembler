package snaypertihcreator.thedisassember;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassember.client.screens.Tier1DisassemblerScreen;
import snaypertihcreator.thedisassember.client.screens.Tier2DisassemblerScreen;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.menus.ModMenuTypes;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = TheDisassemberMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.TIER1_DISASSEMBLER_MENU.get(), Tier1DisassemblerScreen::new));
        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.TIER2_DISASSEMBLER_MENU.get(), Tier2DisassemblerScreen::new));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        registerComponetColor(event, ModItems.TEETH_ITEMS.values(),
                item -> item.getMaterial().getColor());

        registerComponetColor(event, ModItems.BLADE_ITEMS.values(),
                item -> item.getMaterial().getColor());

        registerToolColor(event, ModItems.SAW_ITEMS.values(),
                (item, stack) -> item.getCore(stack).getColor(),
                (item, stack) -> item.getTeeth(stack).getColor());
    }

    private static <T extends Item> void registerComponetColor(
            RegisterColorHandlersEvent.Item event,
            Collection<RegistryObject<T>> objects,
            Function<T, Integer> colorProvider)
    {
        Item[] items = objects.stream().map(RegistryObject::get).toArray(Item[]::new);
        event.register((stack, tintIndex) -> {
            @SuppressWarnings("unchecked")
            T item = (T) stack.getItem();
            return tintIndex == 0 ? colorProvider.apply(item) : -1;
        }, items);
    }

    private static <T extends Item> void  registerToolColor(
            RegisterColorHandlersEvent.Item event,
            Collection<RegistryObject<T>> objects,
            BiFunction<T, ItemStack, Integer> colorProvider1,
            BiFunction<T, ItemStack, Integer> colorProvider2)
    {
        Item[] items = objects.stream().map(RegistryObject::get).toArray(Item[]::new);
        event.register((stack, tintIndex) -> {
            @SuppressWarnings("unchecked")
            T item = (T) stack.getItem();
            return switch (tintIndex) {
                case 0 -> colorProvider1.apply(item, stack);
                case 1 -> colorProvider2.apply(item, stack);
                default -> -1;
            };
        }, items);
    }
}
