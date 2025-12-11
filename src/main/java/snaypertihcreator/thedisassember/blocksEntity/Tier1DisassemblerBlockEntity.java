package snaypertihcreator.thedisassember.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.menus.Tier1DisassemblerMenu;

public class Tier1DisassemblerBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler handler = new ItemStackHandler(10){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> lazyHandler = LazyOptional.of(() -> handler);

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public Tier1DisassemblerBlockEntity(BlockPos pos, BlockState state){
        super(ModBlocksEntity.TIER1_DISASSEMBER_BE.get(), pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> Tier1DisassemblerBlockEntity.this.progress;
                    case 1 -> Tier1DisassemblerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> Tier1DisassemblerBlockEntity.this.progress = value;
                    case 1 -> Tier1DisassemblerBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void spined(){
        if (handler.getStackInSlot(0).isEmpty()){
            setChanged();
            return;
        }
        if (!hasRecipe(this)){
            setChanged();
            return;
        }

        this.progress += 5;
        if (this.progress >= this.maxProgress){
            craftItem(this);
            this.progress = 0;
        }
        setChanged();
    }



    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHandler.invalidate();
    }

    private static boolean hasRecipe(Tier1DisassemblerBlockEntity entity) {
        ItemStack inputItem = entity.handler.getStackInSlot(0); // Слот 0 - Вход
        if (inputItem.isEmpty()) return true;
        ItemStack result = new ItemStack(net.minecraft.world.item.Items.DIAMOND);
        return canInsertItemIntoOutputSlots(entity.handler, result);
    }

    private static boolean canInsertItemIntoOutputSlots(ItemStackHandler handler, ItemStack output) {
        for (int i = 1; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.isEmpty() ||
                    (stackInSlot.getItem() == output.getItem() && stackInSlot.getCount() < stackInSlot.getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    private static void craftItem(Tier1DisassemblerBlockEntity entity) {
        if (!hasRecipe(entity)) return;
        entity.handler.extractItem(0, 1, false);
        ItemStack result = new ItemStack(net.minecraft.world.item.Items.DIAMOND);
        for (int i = 1; i < entity.handler.getSlots(); i++) {
            result = entity.handler.insertItem(i, result, false);
            if (result.isEmpty()) {
                break;
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, Tier1DisassemblerBlockEntity entity) {
        if (level.isClientSide()) return;
        if (entity.progress <= 0) return;

        entity.progress --;
        setChanged(level, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", handler.serializeNBT());
        nbt.putInt("progress", progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        handler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("progress");
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu."+TheDisassemberMod.MODID+".base_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory inventory, @NotNull Player player) {
        return new Tier1DisassemblerMenu(containerID, inventory, this, this.data);
    }

}
