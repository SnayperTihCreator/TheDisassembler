package snaypertihcreator.thedisassember;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.blocksEntity.ModBlocksEntity;
import snaypertihcreator.thedisassember.creativetabs.ModCreativeTabs;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.menus.ModMenuTypes;
import snaypertihcreator.thedisassember.networking.ModMessages;
import snaypertihcreator.thedisassember.providers.*;

import java.util.concurrent.CompletableFuture;


@Mod(TheDisassemberMod.MODID)
public class TheDisassemberMod
{
    public static final String MODID = "thedisassember";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TheDisassemberMod(FMLJavaModLoadingContext context)
    {
        IEventBus bus = context.getModEventBus();
        bus.addListener(this::commonSetup);

        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModBlocksEntity.BLOCKS_ENTITY.register(bus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(bus);
        ModMenuTypes.MENUS.register(bus);
        ModMessages.register();

        bus.addListener(this::gatherData);
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    private void addCreative(BuildCreativeModeTabContentsEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new ModModelProvider(output, helper));
        generator.addProvider(event.includeClient(), new ModRecipeProvider(output));
        generator.addProvider(event.includeClient(), new ModEnLangProvider(output));
        generator.addProvider(event.includeClient(), new ModRuLangProvider(output));
        ModBlockTagProvider bProvider = generator.addProvider(event.includeClient(), new ModBlockTagProvider(output, provider, helper));
        generator.addProvider(event.includeClient(), new ModItemTagProvider(output, provider,bProvider.contentsGetter(), helper));

    }
}
