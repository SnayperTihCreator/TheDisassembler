package snaypertihcreator.thedisassembler.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.items.DistillationKitItem;
import snaypertihcreator.thedisassembler.items.ModItems;
import snaypertihcreator.thedisassembler.recipes.DistillationRecipe;
import snaypertihcreator.thedisassembler.recipes.ModRecipes;

import java.util.Optional;
import java.util.Random;

public abstract class ExtractorBlockEntity extends BlockEntity implements MenuProvider {

    protected static final Random RANDOM = new Random();

    protected final ItemStackHandler handler;
    protected final LazyOptional<IItemHandler> internalLazyHandler;
    protected final LazyOptional<IItemHandler> automationLazyHandler;

    protected int progress = 0;
    protected int maxProgress = 100;
    protected float currentTemp = 20.0F;
    protected final ContainerData data;

    @Nullable
    protected DistillationRecipe cachedRecipe;

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_KIT = 1;

    public ExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slotCount){
        super(type, pos, state);

        handler = new ItemStackHandler(slotCount){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                ExtractorBlockEntity.this.onInventoryChange(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return ExtractorBlockEntity.this.isItemValid(slot, stack);
            }
        };

        internalLazyHandler = LazyOptional.of(() -> handler);

        this.automationLazyHandler = LazyOptional.of(() -> new IItemHandlerModifiable() {
            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                handler.setStackInSlot(slot, stack);
            }
            @Override
            public int getSlots() { return handler.getSlots(); }
            @Override
            public @NotNull ItemStack getStackInSlot(int slot) { return handler.getStackInSlot(slot); }
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (!canAutomationInsert(slot, stack)) return stack;
                return handler.insertItem(slot, stack, simulate);
            }
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!canAutomationExtract(slot)) return ItemStack.EMPTY;
                return handler.extractItem(slot, amount, simulate);
            }
            @Override
            public int getSlotLimit(int slot) { return handler.getSlotLimit(slot); }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return canAutomationInsert(slot, stack) && handler.isItemValid(slot, stack);
            }
        });

        data = createContainerData();
    }

    protected abstract float getTargetTemperature();
    protected abstract float getHeatSpeed();
    protected boolean serverTickFuel() {return true;}
    protected abstract int getOutputSlot();
    @SuppressWarnings("unused")
    protected void updateBlockState(BlockState state, boolean isWorking, boolean isBurning) {}
    protected abstract ContainerData createContainerData();

    protected void onInventoryChange(int slot){
        if (slot == SLOT_INPUT) {
            this.progress = 0;
            updateRecipeCache();
        }
    }

    private void updateRecipeCache() {
        if (level == null || level.isClientSide) return;

        ItemStack input = handler.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty() || input.getItem() == Items.GLASS_BOTTLE) {
            cachedRecipe = null;
            return;
        }

        SimpleContainer tempContainer = new SimpleContainer(1);
        tempContainer.setItem(0, input);

        Optional<DistillationRecipe> match = level.getRecipeManager().getRecipeFor(ModRecipes.DISTILLATION_TYPE.get(), tempContainer, level);

        cachedRecipe = match.orElse(null);
    }

    protected boolean isItemValid(int slot, ItemStack stack){
        if (slot == SLOT_INPUT) return PotionUtils.getPotion(stack) != Potions.EMPTY && PotionUtils.getPotion(stack) != Potions.WATER;
        if (slot == SLOT_KIT) return stack.getItem() instanceof DistillationKitItem;
        return true;
    }

    protected boolean canAutomationInsert(int slot, ItemStack stack) {
        if (slot == SLOT_INPUT) return isItemValid(SLOT_INPUT, stack);
        if (slot == SLOT_KIT) return isItemValid(SLOT_KIT, stack);
        return slot != getOutputSlot();
    }

    protected boolean canAutomationExtract(int slot) {
        if (slot == getOutputSlot()) return true;
        if (slot == SLOT_INPUT) return handler.getStackInSlot(slot).getItem() == Items.GLASS_BOTTLE;
        return false;
    }


    public static void tick(Level level, BlockPos ignoredPos, BlockState state, ExtractorBlockEntity entity){
        if (level.isClientSide) return;

        if (entity.cachedRecipe == null && !entity.handler.getStackInSlot(SLOT_INPUT).isEmpty()) entity.updateRecipeCache();

        boolean isBurning = entity.serverTickFuel();

        float target = entity.getTargetTemperature();
        float speed = entity.getHeatSpeed();

        // Физика тепла
        if (entity.currentTemp < target) entity.currentTemp = Math.min(entity.currentTemp + speed, target);
        else if (entity.currentTemp > target) entity.currentTemp = Math.max(entity.currentTemp - speed, target);

        // Проверки
        boolean canWork = isBurning
                && entity.canDisassembleCurrentItem()
                && entity.hasFreeOutputSlot()
                && entity.hasRequiredTools();

        entity.updateBlockState(state, canWork, isBurning);

        if (canWork && entity.cachedRecipe != null){
            float optimal = entity.cachedRecipe.getTemperature();

            if (entity.currentTemp >= optimal - 20) {
                entity.progress++;
                if (entity.progress >= entity.maxProgress) entity.finishCrafting(optimal);
            } else if (entity.progress > 0) entity.progress--;
        } else entity.progress = 0;
    }


    protected boolean canDisassembleCurrentItem(){
        ItemStack input = handler.getStackInSlot(SLOT_INPUT);
        return !input.isEmpty() && input.getItem() != Items.GLASS_BOTTLE;
    }

    protected boolean hasFreeOutputSlot(){
        ItemStack outputStack = handler.getStackInSlot(getOutputSlot());
        if (outputStack.isEmpty()) return true;
        if (outputStack.getItem() == ModItems.BREWING_SEDIMENT.get()) return outputStack.getCount() < outputStack.getMaxStackSize();
        return false;
    }

    protected boolean hasRequiredTools(){
        return !handler.getStackInSlot(SLOT_KIT).isEmpty();
    }

    protected float calculateBurnFactor(float optimalTemp) {
        float diff = currentTemp - optimalTemp;
        if (diff > 50) return Math.max(0.0f, 1.0f - ((diff - 50) / 200.0f));
        return 1.0f;
    }

    protected float processKit() {
        ItemStack kitStack = handler.getStackInSlot(SLOT_KIT);

        float kitEff = 0.1f;

        if (kitStack.getItem() instanceof DistillationKitItem kit) {
            kitEff = kit.getEfficiency();
            if (kitStack.isDamageableItem() && level != null && kitStack.hurt(1, level.random, null))
                kitStack.shrink(1);
        }

        return kitEff;
    }

    protected void finishCrafting(float optimalTemp) {
        if (cachedRecipe == null) updateRecipeCache();
        if (cachedRecipe == null) return;

        float kitEff = processKit();
        float burnFactor = calculateBurnFactor(optimalTemp);

        ItemStack result = cachedRecipe.assembleSediment(handler.getStackInSlot(SLOT_INPUT), kitEff, burnFactor, RANDOM);

        if (handler.insertItem(getOutputSlot(), result, false).isEmpty()) {
            handler.extractItem(SLOT_INPUT, 1, false);
            handler.insertItem(SLOT_INPUT, new ItemStack(Items.GLASS_BOTTLE), false);
            this.progress = 0;
            setChanged();
        }
    }


    public void clearContent() {
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
        progress = 0;
        setChanged();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return side == null ? internalLazyHandler.cast() : automationLazyHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        internalLazyHandler.invalidate();
        automationLazyHandler.invalidate();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        handler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        currentTemp = tag.getFloat("temp");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", handler.serializeNBT());
        tag.putInt("progress", progress);
        tag.putFloat("temp", currentTemp);
        super.saveAdditional(tag);
    }
}
