package snaypertihcreator.thedisassembler.recipes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ItemStackJSWrapper {
    private final ItemStack stack;

    public ItemStackJSWrapper(ItemStack stack) {
        this.stack = stack;
    }

    public String getId() {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
    }

    public CompoundTag getTag() {
        return stack.getTag() != null ? stack.getTag().copy() : new CompoundTag();
    }

    public int getCount() {
        return stack.getCount();
    }

    public ItemStack getRealStack() {
        return stack;
    }

    @Override
    public String toString() {
        return String.format("ItemStack{id=%s, count=%d, nbt=%s}",
                getId(), getCount(), getTag());
    }
}
