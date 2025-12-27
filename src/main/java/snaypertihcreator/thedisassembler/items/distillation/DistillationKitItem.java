package snaypertihcreator.thedisassembler.items.distillation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DistillationKitItem extends Item {

    private final DistillationTier tier;

    public DistillationKitItem(DistillationTier tier) {
        super(createProps(tier));
        this.tier = tier;
    }

    private static Item.Properties createProps(DistillationTier tier) {
        Item.Properties props = new Item.Properties()
                .durability(tier.getDurability())
                .rarity(tier.getRarity())
                .setNoRepair();

        if (tier.isFireResistant()) {
            props.fireResistant();
        }

        return props;
    }

    public float getEfficiency() {
        return tier.getEfficiency();
    }

    // Геттер тира (вдруг пригодится для проверок)
    public DistillationTier getTier() {
        return tier;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        String percent = String.format("%.0f%%", tier.getEfficiency() * 100);

        // Красим цифры в зависимости от крутости тира
        ChatFormatting color = switch (tier) {
            case GLASS -> ChatFormatting.RED;
            case REINFORCED -> ChatFormatting.YELLOW;
            case INDUSTRIAL -> ChatFormatting.GREEN;
        };

        tooltipComponents.add(
                Component.translatable("tooltip.thedisassembler.max_efficiency",
                                Component.literal(percent).withStyle(color)).withStyle(ChatFormatting.GRAY));
    }
}