package snaypertihcreator.thedisassembler;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import snaypertihcreator.thedisassembler.items.BrewingSedimentItem;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.menus.ModMenuTypes;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

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
                item -> item.getMaterial().getColor());

        registerComponentColor(event, ModItems.BLADE_ITEMS.values(),
                item -> item.getMaterial().getColor());

        registerToolColor(event, ModItems.SAW_ITEMS.values(),
                (item, stack) -> item.getCore(stack).getColor(),
                (item, stack) -> item.getTeeth(stack).getColor());

        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                int color = BrewingSedimentItem.getColor(stack);
                if (color == 0xAAAAAA || color == 0xA0A0A0) {
                    var contents = BrewingSedimentItem.getContents(stack);
                    if (!contents.isEmpty()) return getIngredientColor(contents.get(0).item());
                }
                return color;
            }
            return -1;
        }, ModItems.BREWING_SEDIMENT.get());
    }

    // Хардкодная палитра для ингредиентов (на случай если в NBT не записали цвет зелья)
    private static int getIngredientColor(Item item) {
        if (item == Items.NETHER_WART) return 0xFF5555;      // Адский нарост (Красный)
        if (item == Items.SUGAR) return 0xFFFFFF;            // Сахар (Белый)
        if (item == Items.RABBIT_FOOT) return 0xC69666;      // Лапка (Бежевый)
        if (item == Items.BLAZE_POWDER) return 0xFFAA00;     // Огненный порошок (Оранжевый)
        if (item == Items.SPIDER_EYE) return 0xA62626;       // Паучий глаз (Темно-красный)
        if (item == Items.FERMENTED_SPIDER_EYE) return 0x3E5421; // Гнилой глаз
        if (item == Items.GHAST_TEAR) return 0xEBF7F7;       // Слеза Гаста
        if (item == Items.MAGMA_CREAM) return 0xD67016;      // Магма
        if (item == Items.GLISTERING_MELON_SLICE) return 0xDDEE11; // Арбуз
        if (item == Items.GOLDEN_CARROT) return 0xEEDD22;    // Морковь
        if (item == Items.PUFFERFISH) return 0xDDBD33;       // Иглобрюх
        if (item == Items.TURTLE_HELMET) return 0x44CC44;    // Черепаха
        if (item == Items.PHANTOM_MEMBRANE) return 0xDDEEFF; // Мембрана
        if (item == Items.GUNPOWDER) return 0x555555;        // Порох
        if (item == Items.GLOWSTONE_DUST) return 0xFFCC00;   // Светопыль
        if (item == Items.REDSTONE) return 0xFF0000;         // Редстоун
        if (item == Items.DRAGON_BREATH) return 0xE88F96;    // Дыхание дракона

        return 0x808080; // Серый, если хз что это
    }



    //Метод для регистрации цвета компонента
    private static <T extends Item> void registerComponentColor(
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
