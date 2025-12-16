package snaypertihcreator.thedisassember.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HandSawItem extends Item {
    private static final String NBT_CORE = "SawCore";
    private static final String NBT_TEETH = "SawTeeth";

    private final SawMaterial core;
    private final SawMaterial teeth;

    public HandSawItem(SawMaterial core, SawMaterial teeth) {
        super(new Properties().durability(core.getMaxUses()));
        this.core = core;
        this.teeth = teeth;
    }

    public SawMaterial getCore(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(NBT_CORE)) {
            try {
                return SawMaterial.valueOf(stack.getTag().getString(NBT_CORE));
            } catch (Exception ignored) {}
        }
        return core;
    }
    public SawMaterial getTeeth(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(NBT_TEETH)) {
            try {
                return SawMaterial.valueOf(stack.getTag().getString(NBT_TEETH));
            } catch (Exception ignored) {}
        }
        return teeth;
    }

    public int getToolLevel(ItemStack stack) {
        return getTeeth(stack).getTierLevel();
    }

    public static ItemStack createSaw(SawMaterial core, SawMaterial teeth) {
        ItemStack stack = new ItemStack(ModItems.SAW_ITEMS.get(SawMaterial.WOOD).get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_CORE, core.name());
        tag.putString(NBT_TEETH, teeth.name());
        stack.setTag(tag);
        return stack;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getCore(stack).getMaxUses();
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pToRepair, @NotNull ItemStack pRepair) {
        return getCore(pToRepair).getRepairIngredient().test(pRepair) || super.isValidRepairItem(pToRepair, pRepair);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        SawMaterial core = getCore(stack);
        SawMaterial teeth = getTeeth(stack);

        if (core == teeth && this.core == core) {
            return super.getName(stack);
        }

        Component coreName = Component.translatable("item.thedisassember." + core.getName() + "_saw");
        return coreName.copy().append(Component.literal(" (" + teeth.getLang().getRuName().plural() + ")"));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.thedisassember.saw.description").withStyle(ChatFormatting.GRAY));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.empty());
            SawMaterial c = getCore(stack);
            SawMaterial t = getTeeth(stack);

            tooltip.add(Component.translatable("tooltip.thedisassember.material.core")
                    .append(Component.literal(" " + c.getLang().getEnName())).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.thedisassember.material.teeth")
                    .append(Component.literal(" " + t.getLang().getEnName())).withStyle(ChatFormatting.AQUA));

            int max = getMaxDamage(stack);
            int current = max - stack.getDamageValue();
            tooltip.add(Component.translatable("tooltip.thedisassember.durability")
                    .append(Component.literal(" " + current + "/" + max)).withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("tooltip.thedisassember.hold_shift").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) { return stack.isDamaged(); }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13.0F - (float)stack.getDamageValue() * 13.0F / (float)this.getMaxDamage(stack));
    }
    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float f = Math.max(0.0F, ((float)this.getMaxDamage(stack) - (float)stack.getDamageValue()) / (float)this.getMaxDamage(stack));
        return net.minecraft.util.Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }
}

