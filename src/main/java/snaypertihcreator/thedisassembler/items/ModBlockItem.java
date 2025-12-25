package snaypertihcreator.thedisassembler.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModBlockItem extends BlockItem {
    private final String tooltipKey;

    public ModBlockItem(Block block, String tooltipKey) {
        super(block, new Properties());
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!tooltipKey.isEmpty()) tooltip.add(Component.translatable(tooltipKey));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
