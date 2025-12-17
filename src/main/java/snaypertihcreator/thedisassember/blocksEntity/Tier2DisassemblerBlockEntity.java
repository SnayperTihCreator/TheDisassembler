package snaypertihcreator.thedisassember.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.items.HandSawItem;
import snaypertihcreator.thedisassember.menus.Tier2DisassemblerMenu;

import java.util.stream.IntStream;

public class Tier2DisassemblerBlockEntity extends DisassemblerBlockEntity {

    private int burnTime;
    private int burnDuration;
    private boolean isActive;

    public Tier2DisassemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocksEntity.TIER2_DISASSEMBER_BE.get(), pos, state, 12);
    }

    public boolean isActive() {return this.isActive;}

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> Tier2DisassemblerBlockEntity.this.burnTime;
                    case 1 -> Tier2DisassemblerBlockEntity.this.burnDuration;
                    case 2 -> Tier2DisassemblerBlockEntity.this.progress;
                    case 3 -> Tier2DisassemblerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> Tier2DisassemblerBlockEntity.this.burnTime = value;
                    case 1 -> Tier2DisassemblerBlockEntity.this.burnDuration = value;
                    case 2 -> Tier2DisassemblerBlockEntity.this.progress = value;
                    case 3 -> Tier2DisassemblerBlockEntity.this.maxProgress = value;
                }
            }
            public int getCount() { return 4; }
        };
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0) return true; // Вход
        if (slot == 1) return ForgeHooks.getBurnTime(stack, null) > 0; // Топливо
        if (slot == 2) return stack.getItem() instanceof HandSawItem; // Диск
        return true;
    }

    @Override
    public int getInputSlot() { return 0; }

    @Override
    public int[] getOutputSlots() {
        return IntStream.range(3, 12).toArray();
    }

    @Override
    protected boolean canAutomationInsert(int slot) {
        return slot < 3;
    }

    public ItemStack getRenderSaw(){
        return handler.getStackInSlot(2);
    }

    @Override
    protected void onInventoryChanged(int slot) {
        if ((slot == 2) && (level != null) && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    public static void tick(@NotNull Level level, BlockPos pos, BlockState state, Tier2DisassemblerBlockEntity entity) {
        if (level.isClientSide) return;

        boolean wasActive = entity.isActive;
        boolean dirty = false;

        if (entity.isBurning()) {
            entity.burnTime--;
            dirty = true;
        }

        ItemStack fuel = entity.handler.getStackInSlot(1);
        ItemStack disk = entity.handler.getStackInSlot(2);

        boolean hasInputAndDisk = entity.canDisassembleCurrentItem() && !disk.isEmpty();

        if (!entity.isBurning() && !fuel.isEmpty() && hasInputAndDisk) {
            if (entity.hasFreeOutputSlot()) {
                entity.burnTime = ForgeHooks.getBurnTime(fuel, null);
                entity.burnDuration = entity.burnTime;

                if (entity.isBurning()) {
                    dirty = true;
                    ItemStack remainder = fuel.getCraftingRemainingItem();
                    fuel.shrink(1);
                    if (fuel.isEmpty()) entity.handler.setStackInSlot(1, remainder);
                }
            }
        }
        boolean canWork = entity.isBurning() && hasInputAndDisk && entity.hasFreeOutputSlot();
        entity.isActive = canWork;

        if (canWork) {
            entity.progress++;
            int speedModifier = 0;
            if (disk.getItem() instanceof HandSawItem sawItem) {
                speedModifier = sawItem.getToolLevel(disk);
            }
            entity.maxProgress = Math.max(20, 200 - (speedModifier * 30));

            if (entity.progress >= entity.maxProgress) {
                entity.progress = 0;
                entity.tryDisassembleCurrentItem();
                if (disk.isDamageableItem()) {
                    if (disk.hurt(1, level.random, null)) {
                        disk.shrink(1);
                        disk.setDamageValue(0);
                    }
                }
                dirty = true;
            }
        } else if (entity.progress > 0) {
            entity.progress = Math.max(0, entity.progress - 2);
        }

        if (wasActive != entity.isActive) {
            dirty = true;
            level.sendBlockUpdated(pos, state, state, 3);
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }

    private boolean hasFreeOutputSlot() {
        for (int slot : getOutputSlots()) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("burnDuration", burnDuration);
        nbt.putBoolean("isActive", isActive);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        burnTime = nbt.getInt("burnTime");
        burnDuration = nbt.getInt("burnDuration");
        isActive = nbt.getBoolean("isActive");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu." + TheDisassemberMod.MODID + ".tier2_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new Tier2DisassemblerMenu(id, inv, this, this.data);
    }
}