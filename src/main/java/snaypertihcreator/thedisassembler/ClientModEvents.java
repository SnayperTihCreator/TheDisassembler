package snaypertihcreator.thedisassembler;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import snaypertihcreator.thedisassembler.blocksEntity.ModBlocksEntity;
import snaypertihcreator.thedisassembler.client.renderers.DisassemblerSawRenderer;
import snaypertihcreator.thedisassembler.client.screens.Tier1DisassemblerScreen;
import snaypertihcreator.thedisassembler.client.screens.Tier2DisassemblerScreen;
import snaypertihcreator.thedisassembler.client.screens.Tier3DisassemblerScreen;
import snaypertihcreator.thedisassembler.items.distillation.BrewingSedimentItem;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.menus.ModMenuTypes;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

// регистрация окна для меню
@Mod.EventBusSubscriber(modid = TheDisassemblerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.TIER1_DISASSEMBLER_MENU.get(), Tier1DisassemblerScreen::new));
        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.TIER2_DISASSEMBLER_MENU.get(), Tier2DisassemblerScreen::new));
        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.TIER3_DISASSEMBLER_MENU.get(), Tier3DisassemblerScreen::new));

    }
    // кастомный рендер для блока
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlocksEntity.TIER2_DISASSEMBLER_BE.get(),
                DisassemblerSawRenderer::new);

        event.registerBlockEntityRenderer(ModBlocksEntity.TIER3_DISASSEMBLER_BE.get(),
                DisassemblerSawRenderer::new);

    }
    // регистрация установки цвета предметам от материала
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {

        registerComponentColor(event, ModItems.TEETH_ITEMS.values(),
                (item, stack) -> item.getMaterial().getColor());

        registerComponentColor(event, ModItems.CORE_ITEMS.values(),
                (item, stack) -> item.getMaterial().getColor());

        registerToolColor(event, ModItems.SAW_ITEMS.values(),
                (item, stack) -> item.getCore(stack).getColor(),
                (item, stack) -> item.getTeeth(stack).getColor());

        registerComponentColor(event, List.of(ModItems.BREWING_SEDIMENT),
                (item, stack) -> BrewingSedimentItem.getColor(stack));
    }
    //Метод для регистрации цвета компонента
    private static <T extends Item> void registerComponentColor(
            RegisterColorHandlersEvent.Item event,
            Collection<RegistryObject<T>> objects,
            BiFunction<T, ItemStack, Integer> colorProvider)
    {
        Item[] items = objects.stream().map(RegistryObject::get).toArray(Item[]::new);
        event.register((stack, tintIndex) -> {
            @SuppressWarnings("unchecked")
            T item = (T) stack.getItem();
            return tintIndex == 0 ? colorProvider.apply(item, stack) : -1;
        }, items);
    }

    //Метод для регистрации цвета самой пилы
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
