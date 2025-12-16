package snaypertihcreator.thedisassember.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import snaypertihcreator.thedisassember.TheDisassemberMod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = TheDisassemberMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TranslationChecker {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Запускаем проверку в отдельном потоке или просто в конце настройки
        event.enqueueWork(() -> {
            System.out.println("==========================================");
            System.out.println("THE DISASSEMBLER: ПРОВЕРКА ПЕРЕВОДОВ");
            System.out.println("==========================================");

            List<String> missing = new ArrayList<>();
            for (Item item : ForgeRegistries.ITEMS) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                if (id != null && id.getNamespace().equals(TheDisassemberMod.MODID)) {
                    String key = item.getDescriptionId();
                    if (!I18n.exists(key)) {
                        missing.add("[ITEM] " + id + " -> " + key);
                    }
                }
            }

            for (Block block : ForgeRegistries.BLOCKS) {
                ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                if (id != null && id.getNamespace().equals(TheDisassemberMod.MODID)) {
                    String key = block.getDescriptionId();
                    if (!I18n.exists(key)) {
                        // Не дублируем, если уже нашли через Item
                        boolean alreadyFound = missing.stream().anyMatch(s -> s.contains(key));
                        if (!alreadyFound) {
                            missing.add("[BLOCK] " + id + " -> " + key);
                        }
                    }
                }
            }

            if (missing.isEmpty()) {
                System.out.println("ВСЕ ОТЛИЧНО! Все предметы и блоки переведены.");
            } else {
                System.out.println("НАЙДЕНО " + missing.size() + " ОТСУТСТВУЮЩИХ ПЕРЕВОДОВ:");
                for (String s : missing) {
                    System.out.println(s);
                }
                System.out.println("Добавьте эти ключи в en_us.json и ru_ru.json!");
            }
            System.out.println("==========================================");
        });
    }
}