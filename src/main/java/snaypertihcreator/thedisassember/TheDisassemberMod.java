package snaypertihcreator.thedisassember;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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
import snaypertihcreator.thedisassember.recipes.ModRecipes;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Mod(TheDisassemberMod.MODID)
public class TheDisassemberMod
{
    public static final String MODID = "thedisassember";
    @SuppressWarnings("unused")
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
        ModRecipes.SERIALIZERS.register(bus);
        ModMessages.register();

        bus.addListener(this::gatherData);
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);

        context.registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC, "thedisassember-common.toml");
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

        boolean includeClient = event.includeClient();

        generator.addProvider(includeClient, new ModModelProvider(output, helper));
        generator.addProvider(includeClient, new ModEnLangProvider(output));
        generator.addProvider(includeClient, new ModRuLangProvider(output));

        boolean includeServer = event.includeServer();

        ModBlockTagProvider blockTags = new ModBlockTagProvider(output, provider, helper);
        generator.addProvider(includeServer, blockTags);
        generator.addProvider(includeServer, new ModItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
        generator.addProvider(includeServer, new ModRecipeProvider(output));

        List<LootTableProvider.SubProviderEntry> lstProviders = java.util.List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)
        );
        generator.addProvider(includeServer, new LootTableProvider(
                output,
                java.util.Set.of(),
                lstProviders
        ));
    }
}
