package snaypertihcreator.thedisassembler.recipes;

import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import snaypertihcreator.thedisassembler.items.disassembler.HandSawItem;

import java.util.Objects;

/**
 * Ультимативная обертка. Написана так, чтобы JS мог достать ВООБЩЕ всё.
 */
public class ItemStackJSWrapper {
    private final ItemStack stack;

    public ItemStackJSWrapper(ItemStack stack) {
        this.stack = stack;
    }

    // --- Базовые свойства ---
    public String getId() { return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString(); }
    public int getCount() { return stack.getCount(); }
    public int getDamage() { return stack.getDamageValue(); }
    public int getMaxDamage() { return stack.getMaxDamage(); }
    public boolean isDamaged() { return stack.isDamaged(); }
    public boolean hasTag() { return stack.hasTag(); }

    // Процент прочности (от 0.0 до 1.0), удобно для расчетов шансов в JS
    public double getDurabilityFactor() {
        if (!stack.isDamageableItem()) return 1.0;
        return (double) (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();
    }

    public int getEnchLevel(String id) {
        var enchantments = EnchantmentHelper.getEnchantments(stack);
        for (var entry : enchantments.entrySet()) {
            ResourceLocation enchKey = ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey());

            if (enchKey != null && enchKey.toString().equals(id)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    // --- Специфика мода The Disassembler (Пилы) ---
    public String getSawCore() {
        return (stack.getItem() instanceof HandSawItem saw) ? saw.getCore(stack).name() : "NONE";
    }

    public String getSawTeeth() {
        return (stack.getItem() instanceof HandSawItem saw) ? saw.getTeeth(stack).name() : "NONE";
    }

    // --- Глубокий доступ к NBT (Deep Access) ---

    public double getNumber(String path) {
        Tag tag = getTagByPath(path);
        return (tag instanceof NumericTag n) ? n.getAsDouble() : 0;
    }

    public int getInt(String path) { return (int) getNumber(path); }

    public String getString(String path) {
        Tag tag = getTagByPath(path);
        return tag != null ? tag.getAsString() : "";
    }

    public int getListSize(String path) {
        Tag tag = getTagByPath(path);
        return (tag instanceof ListTag l) ? l.size() : 0;
    }

    /**
     * Возвращает массив чисел из NBT (например, Colors в фейерверках).
     * JS увидит это как обычный массив.
     */
    public int[] getIntArray(String path) {
        Tag tag = getTagByPath(path);
        return (tag instanceof IntArrayTag a) ? a.getAsIntArray() : new int[0];
    }

    private Tag getTagByPath(String path) {
        if (!stack.hasTag() || path == null || path.isEmpty()) return null;
        String[] parts = path.split("\\.");
        Tag current = stack.getTag();
        for (String part : parts) {
            if (current instanceof CompoundTag c && c.contains(part)) {
                current = c.get(part);
            } else return null;
        }
        return current;
    }

    @Override
    public String toString() { return stack.toString(); }
}