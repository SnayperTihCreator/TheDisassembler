package snaypertihcreator.thedisassembler.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.items.ModItems;

import javax.script.*;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class JSRecipeManager {
    private static final JSRecipeManager INSTANCE = new JSRecipeManager();
    private final Map<Item, CompiledScript> itemToScript = new HashMap<>();
    private ScriptEngine engine;
    private Compilable compilable;

    public static JSRecipeManager getInstance() { return INSTANCE; }

    private JSRecipeManager() {
        try {
            ClassLoader loader = getClass().getClassLoader();
            ScriptEngineManager manager = new ScriptEngineManager(loader);
            this.engine = manager.getEngineByName("nashorn");

            if (this.engine == null) {
                Class<?> factoryClass = Class.forName("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory", true, loader);
                this.engine = (ScriptEngine) factoryClass.getMethod("getScriptEngine").invoke(factoryClass.getDeclaredConstructor().newInstance());
            }

            if (this.engine instanceof Compilable c) {
                this.compilable = c;
                TheDisassemblerMod.LOGGER.info("Nashorn JS Engine успешно загружен!");
            }
        } catch (Exception e) {
            TheDisassemblerMod.LOGGER.error("Ошибка инициализации JS движка: ", e);
        }
    }

    public void reload(ResourceManager rm, File configDir) {
        itemToScript.clear();
        if (compilable == null) return;

        // Вшитые рецепты
        registerBuiltin(Items.FIREWORK_ROCKET, "fireworks.js", rm);
        ModItems.SAW_ITEMS.values().forEach(item -> registerBuiltin(item.get(), "saw.js", rm));

        loadExternalScripts(configDir);
    }

    private void loadExternalScripts(File configDir){
        // Внешние рецепты
        File mappingFile = new File(configDir, "recipe_mapping.json");
        if (!mappingFile.exists()) return;

        try {
            JsonObject mapping = JsonParser.parseString(Files.readString(mappingFile.toPath())).getAsJsonObject();
            for (String itemId : mapping.keySet()) {
                File file = new File(new File(configDir, "scripts_extra"), mapping.get(itemId).getAsString());
                Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemId));
                if (file.exists() && item != null) {
                    itemToScript.put(item, compilable.compile(Files.readString(file.toPath())));
                }
            }
        } catch (Exception e) {
            TheDisassemblerMod.LOGGER.error("Ошибка загрузки рецептов из конфига: {}", e.getMessage());
        }
    }

    private void registerBuiltin(Item item, String name, ResourceManager rm) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(TheDisassemblerMod.MODID, "scripts/" + name);
        rm.getResource(loc).ifPresent(res -> {
            try (var reader = new InputStreamReader(res.open(), StandardCharsets.UTF_8)) {
                itemToScript.put(item, compilable.compile(reader));
            } catch (Exception e) {
                TheDisassemblerMod.LOGGER.error("Ошибка компиляции {}: {}", name, e.getMessage());
            }
        });
    }

    /**
     * Универсальный метод вызова JS функций
     */
    private Object safeInvoke(Item item, String function, Object... args) {
        CompiledScript script = itemToScript.get(item);
        if (script == null) return null;

        try {
            // КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ:
            // Сначала выполняем скрипт БЕЗ кастомных Bindings, чтобы функции сели в глобальный контекст
            script.eval();
            return ((Invocable) engine).invokeFunction(function, args);
        } catch (Exception e) {
            TheDisassemblerMod.LOGGER.error("Ошибка в JS ({}) для {}: {}", function, item, e.getMessage());
            return null;
        }
    }

    public List<ItemStack> disassemble(ItemStack input, Random random, float luck) {
        Object result = safeInvoke(input.getItem(), "disassemble", new ItemStackJSWrapper(input), luck, random);

        // ЛОГ ДЛЯ ПРОВЕРКИ
        TheDisassemblerMod.LOGGER.debug("JS Raw Result: {}", result);

        List<ItemStack> list = new ArrayList<>();
        Collection<?> itemsCollection = null;

        // 1. Пытаемся достать коллекцию (Nashorn может вернуть List или Map)
        if (result instanceof List<?> jsList) {
            itemsCollection = jsList;
        } else if (result instanceof Map<?, ?> jsMap) {
            itemsCollection = jsMap.values();
        }

        if (itemsCollection != null) {
            for (Object obj : itemsCollection) {
                // JS объект приходит как Map
                if (obj instanceof Map<?, ?> map) {
                    try {
                        Object idObj = map.get("id");
                        if (idObj == null) continue;

                        // Важно: парсим ID
                        ResourceLocation resLoc = ResourceLocation.tryParse(idObj.toString());
                        if (resLoc == null) continue;

                        Item item = ForgeRegistries.ITEMS.getValue(resLoc);

                        // Безопасно достаем число (даже если оно 3.0 или "3")
                        Object countObj = map.get("count");
                        int count = 1;
                        if (countObj instanceof Number n) {
                            count = n.intValue();
                        } else if (countObj instanceof String s) {
                            count = Integer.parseInt(s);
                        }

                        if (item != null && item != Items.AIR) {
                            list.add(new ItemStack(item, count));
                        }
                    } catch (Exception e) {
                        TheDisassemblerMod.LOGGER.error("Ошибка парсинга предмета из JS: {}", e.getMessage());
                    }
                }
            }
        }

        TheDisassemblerMod.LOGGER.debug("Final list size: {}", list.size());
        return list;
    }

    public int getMinInput(Item item) {
        Object res = safeInvoke(item, "getMinInput");
        return (res instanceof Number n) ? n.intValue() : 1;
    }

    public boolean hasScript(Item item) { return itemToScript.containsKey(item); }
}