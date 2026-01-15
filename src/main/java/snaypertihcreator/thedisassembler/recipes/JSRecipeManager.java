package snaypertihcreator.thedisassembler.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import snaypertihcreator.thedisassembler.ModCommonConfig;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class JSRecipeManager {
    private static final JSRecipeManager INSTANCE = new JSRecipeManager();
    private final Map<String, CompiledScript> recipes = new HashMap<>();

    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private final Compilable compilable = (Compilable) engine;

    private JSRecipeManager(){
        engine.put("modid", TheDisassemblerMod.MODID);
    }

    public static JSRecipeManager getInstance() {
        return INSTANCE;
    }

    public void loadScripts() {
        recipes.clear();
        loadDefaultScripts();
        loadExternalScripts(ModCommonConfig.EXTERNAL_JS_PATH.get());
    }

    private void loadDefaultScripts(){
        List<String> defaultScripts = List.of("firework.js");

        defaultScripts.forEach(scriptFile -> {
            try{
                String scriptName = scriptFile.replace(".js", "");
                String scriptContent = loadResource("extra_recipe/"+scriptFile);
                registerScript(scriptName, scriptContent);
            } catch (IOException | ScriptException e) {
                TheDisassemblerMod.LOGGER.error("Не удалось загрузить дефолтный JS: {}", scriptFile, e);
            }
        });

    }

    private String loadResource(String resourcePath) throws IOException{
        var inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) throw new IOException("Ресурс не найден: " + resourcePath);

        return new String(inputStream.readAllBytes());
    }

    private void loadExternalScripts(String path){
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
            return;
        }

        File[] files = dir.listFiles(file -> file.getName().endsWith(".js"));
        if (files == null) return;

        Arrays.stream(files).forEach(file -> {
            try {
                String scriptName = file.getName().replace(".js", "");
                String scriptContent = Files.readString(file.toPath());
                registerScript(scriptName, scriptContent);
            } catch (IOException e) {
                TheDisassemblerMod.LOGGER.error("Не удалось загрузить JS: {}", file, e);
            } catch (ScriptException e) {
                TheDisassemblerMod.LOGGER.error("Ошибка компиляции JS рецепта {}", file.getName(), e);
            }
        });
    }

    private void registerScript(String name, String scriptContent) throws ScriptException {
        CompiledScript compiled = compilable.compile(scriptContent);
        recipes.put(name, compiled);
    }

    public List<ItemStack> disassemble(ItemStack stack) {
        String scriptId = getScriptId(stack);
        CompiledScript script = recipes.get(scriptId);
        if (script == null) {
            TheDisassemblerMod.LOGGER.warn("JS рецепт не найден для: {}", ForgeRegistries.ITEMS.getKey(stack.getItem()));
            return List.of();
        }

        try {
            // Создаём контекст для выполнения
            ScriptContext context = new SimpleScriptContext();
            Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);

            // Передаём ItemStack в JS через обёртку
            ItemStackJSWrapper wrapper = new ItemStackJSWrapper(stack);
            bindings.put("stack", wrapper);

            // Выполняем скрипт
            script.eval(context);

            // Вызываем функцию disassemble()
            Invocable invocable = (Invocable) engine;
            Object result = invocable.invokeFunction("disassemble", wrapper);

            return convertResult(result);

        } catch (NoSuchMethodException e) {
            TheDisassemblerMod.LOGGER.error("Функция disassemble() не найдена в скрипте: {}", scriptId, e);
        } catch (ScriptException | IllegalArgumentException e) {
            TheDisassemblerMod.LOGGER.error("Ошибка выполнения JS для {}: {}", scriptId, stack, e);
        }
        return List.of();
    }

    private String getScriptId(ItemStack stack) {
        String itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
        String scriptName = itemId.substring(itemId.indexOf(":") + 1);

        // Проверяем точное совпадение
        if (recipes.containsKey(scriptName)) {
            return scriptName;
        }

        return scriptName;
    }

    private List<ItemStack> convertResult(Object jsResult) {
        if (!(jsResult instanceof List)) {
            TheDisassemblerMod.LOGGER.warn("JS вернул неправильный тип результата: {}", jsResult.getClass());
            return List.of();
        }

        List<?> jsList = (List<?>) jsResult;
        return jsList.stream()
                .filter(obj -> obj instanceof Map)
                .map(obj -> (Map<String, Object>) obj)
                .map(this::mapToItemStack)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ItemStack mapToItemStack(Map<String, Object> dropMap) {
        String id = (String) dropMap.get("id");
        if (id == null) return null;

        ResourceLocation resLoc = ResourceLocation.tryParse(id);
        if (resLoc == null) {
            TheDisassemblerMod.LOGGER.warn("Неверный ID предмета в JS: {}", id);
            return null;
        }

        Item item = ForgeRegistries.ITEMS.getValue(resLoc);
        if (item == null) {
            TheDisassemblerMod.LOGGER.warn("Предмет не найден: {}", id);
            return null;
        }

        int count = dropMap.containsKey("count")
                ? Math.max(1, ((Number) dropMap.get("count")).intValue())
                : 1;

        return new ItemStack(item, Math.min(count, item.getMaxStackSize()));
    }

    // Геттер для отладки
    public Map<String, CompiledScript> getLoadedRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    public boolean supportsItem(ItemStack stack) {
        return ModCommonConfig.JS_TARGETS.get().stream()
                .anyMatch(id -> matches(stack, id));
    }

    public boolean supportsItemId(String itemId) {
        return ModCommonConfig.JS_TARGETS.get().stream()
                .anyMatch(target -> matchesItemId(itemId, target));
    }

    public static boolean isJSTarget(Item item) {
        String itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString();
        return getInstance().supportsItemId(itemId);
    }

    private boolean matches(ItemStack stack, String targetId) {
        if (targetId.startsWith("#")) {
            return false;
        }
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString().equals(targetId);
    }

    private boolean matchesItemId(String itemId, String targetId) {
        if (targetId.startsWith("#")) return false;
        return itemId.equals(targetId);
    }

}
