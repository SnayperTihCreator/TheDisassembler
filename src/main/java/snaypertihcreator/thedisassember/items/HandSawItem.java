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

// Сама пила
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

    // получить материал серцевины
    public SawMaterial getCore(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(NBT_CORE)) {
            try {
                return SawMaterial.valueOf(stack.getTag().getString(NBT_CORE));
            } catch (Exception ignored) {}
        }
        return core;
    }
    // получить материал зубъев
    public SawMaterial getTeeth(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(NBT_TEETH)) {
            try {
                return SawMaterial.valueOf(stack.getTag().getString(NBT_TEETH));
            } catch (Exception ignored) {}
        }
        return teeth;
    }

    // получить уровень скорости предмета
    public int getToolLevel(ItemStack stack) {
        return getTeeth(stack).getSpeedLevel();
    }

    // получить модификатор скорости
    public float getSpeedModifier(ItemStack stack){
        return getCore(stack).getSpeedMultiplier();
    }

    // получить модификатор удачи
    public float getLuckModifier(ItemStack stack){
        return getTeeth(stack).getLuckModifier();
    }

    // метод для создания пилы из материалов
    public static ItemStack createSaw(SawMaterial core, SawMaterial teeth) {
        Item baseItem = ModItems.SAW_ITEMS.get(core).get();
        ItemStack stack = new ItemStack(baseItem);

        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(NBT_CORE, core.name());
        tag.putString(NBT_TEETH, teeth.name());
        stack.setTag(tag);

        return stack;
    }

    // получить макс прочтность
    @Override
    public int getMaxDamage(ItemStack stack) {
        return getCore(stack).getMaxUses();
    }

    // проверить можно ли поченить ли этим предметов
    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pToRepair, @NotNull ItemStack pRepair) {
        return getCore(pToRepair).getRepairIngredient().test(pRepair) || super.isValidRepairItem(pToRepair, pRepair);
    }

    // получить отображаемое имя
    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        SawMaterial core = getCore(stack);
        SawMaterial teeth = getTeeth(stack);

        if (core == teeth && this.core == core) {
            return super.getName(stack);
        }

        Component coreNameComp = Component.translatable("material.thedisassember." + core.getName() + ".adj");
        Component teethNameComp = Component.translatable("material.thedisassember." + teeth.getName() + ".plural");
        return Component.translatable("item.thedisassember.saw_name", coreNameComp, teethNameComp);
    }

    // подсказки
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.thedisassember.saw.description").withStyle(ChatFormatting.GRAY));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.empty());
            SawMaterial c = getCore(stack);
            SawMaterial t = getTeeth(stack);

            Component coreName = Component.translatable("material.thedisassember."+c.getName()+".adj");
            tooltip.add(Component.translatable("tooltip.thedisassember.material.core", coreName)
                    .withStyle(ChatFormatting.GOLD));

            Component teethName = Component.translatable("material.thedisassember."+t.getName()+".plural");
            tooltip.add(Component.translatable("tooltip.thedisassember.material.teeth", teethName)
                    .withStyle(ChatFormatting.AQUA));

            int max = getMaxDamage(stack);
            int current = max - stack.getDamageValue();

            tooltip.add(Component.translatable("tooltip.thedisassember.durability", current, max)
                    .withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("tooltip.thedisassember.hold_shift").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    // все ниже это для бара прочности
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) { return stack.isDamaged(); }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round(13.0F - (float)stack.getDamageValue() * 13.0F / (float)this.getMaxDamage(stack));
    }
    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float f = Math.max(0.0F, ((float)this.getMaxDamage(stack) - stack.getDamageValue()) / this.getMaxDamage(stack));
        return net.minecraft.util.Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }
}

