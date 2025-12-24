package snaypertihcreator.thedisassembler.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BrewingSedimentItem extends Item {
    public BrewingSedimentItem() {
        super(new Properties());
    }

    public record SedimentContent(Item item, float percentage) {}

    /**
     * Сохраняет список ингредиентов в ItemStack.
     */
    public static void setContents(ItemStack stack, List<SedimentContent> contents) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag listTag = new ListTag();

        contents.forEach(content -> {
            CompoundTag contentTag = new CompoundTag();
            contentTag.putString("id", getRegName(content.item));
            contentTag.putFloat("amount", content.percentage);
            listTag.add(contentTag);
        });
        tag.put("sediment_contents", listTag);
    }

    /**
     * Читает список ингредиентов из ItemStack.
     */
    public static List<SedimentContent> getContents(ItemStack stack) {
        List<SedimentContent> contents = new ArrayList<>();
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("sediment_contents")) {
            ListTag listTag = tag.getList("sediment_contents", Tag.TAG_COMPOUND);
            listTag.forEach(ltag -> {
                CompoundTag contentTag = (CompoundTag)ltag;
                Item item = getItemFromRegName(contentTag.getString("id"));
                float amount = contentTag.getFloat("amount");
                if (item != null) contents.add(new SedimentContent(item, amount));
            });
        }
        return contents;
    }

    public static void setColor(ItemStack stack, int color) {
        stack.getOrCreateTag().putInt("color", color);
    }

    public static int getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("color")) return tag.getInt("color");

        List<SedimentContent> contents = getContents(stack);
        if (!contents.isEmpty()) return 0xAAAAAA;
        return 0xA0A0A0;
    }

    // --- Визуал (Тултип) ---

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        List<SedimentContent> contents = getContents(stack);

        if (contents.isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.yourmod.empty_sediment").withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltipComponents.add(Component.translatable("tooltip.yourmod.sediment_contains").withStyle(ChatFormatting.GOLD));
        contents.forEach(content -> {
            String percentString = String.format("%.0f%%", content.percentage * 100);

            Component line = Component.empty()
                    .append(content.item.getDescription()) // Имя предмета
                    .append(Component.literal(": %s".formatted(percentString)).withStyle(ChatFormatting.WHITE));

            tooltipComponents.add(line);
        });
    }

    private static String getRegName(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        return key != null ? key.toString() : "minecraft:air";
    }

    private static Item getItemFromRegName(String id) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id));
    }
}
