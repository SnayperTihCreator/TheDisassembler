package snaypertihcreator.thedisassember.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.Block;
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
import snaypertihcreator.thedisassember.items.HandSawItem;
import snaypertihcreator.thedisassember.items.ModItems;
import snaypertihcreator.thedisassember.items.SawMaterial;
import snaypertihcreator.thedisassember.recipes.DisassemblingRecipe;
import snaypertihcreator.thedisassember.recipes.DisassemblyCache;
import snaypertihcreator.thedisassember.recipes.ModRecipes;

import java.util.*;

public abstract class DisassemblerBlockEntity extends BlockEntity implements MenuProvider {

    protected static final double AUTO_RECIPE_CHANCE = 0.75;
    protected static final Random RANDOM = new Random();

    protected final ItemStackHandler handler;
    protected final LazyOptional<IItemHandler> internalLazyHandler;
    protected final LazyOptional<IItemHandler> automationLazyHandler;

    protected int progress = 0;
    protected int maxProgress = 100;
    protected final ContainerData data;

    public DisassemblerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slotCount) {
        super(type, pos, state);

        this.handler = new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                DisassemblerBlockEntity.this.onInventoryChanged(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return DisassemblerBlockEntity.this.isItemValid(slot, stack);
            }
        };
        this.internalLazyHandler = LazyOptional.of(() -> handler);

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
                if (!canAutomationInsert(slot)) return stack;
                return handler.insertItem(slot, stack, simulate);
            }
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return handler.extractItem(slot, amount, simulate);
            }
            @Override
            public int getSlotLimit(int slot) { return handler.getSlotLimit(slot); }
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return canAutomationInsert(slot) && handler.isItemValid(slot, stack);
            }
        });

        this.data = createContainerData();
    }

    protected abstract ContainerData createContainerData();
    protected abstract boolean canAutomationInsert(int slot);
    protected abstract boolean isItemValid(int slot, ItemStack stack);
    protected abstract void onInventoryChanged(int slot);
    public abstract int getInputSlot();
    public abstract int[] getOutputSlots();

    public boolean canDisassembleCurrentItem() {
        if (level == null) return false;

        ItemStack inputStack = handler.getStackInSlot(getInputSlot());
        if (inputStack.isEmpty()) return false;

        if (inputStack.getItem() instanceof HandSawItem) return true;

        SimpleContainer tempContainer = new SimpleContainer(1);
        tempContainer.setItem(0, inputStack);
        Optional<DisassemblingRecipe> customRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.DISASSEMBLING_TYPE, tempContainer, level);

        if (customRecipe.isPresent()) {
            return inputStack.getCount() >= customRecipe.get().getCountInput();
        }

        CraftingRecipe autoRecipe = DisassemblyCache.getRecipe(inputStack);
        if (autoRecipe != null) {
            int craftingResultCount = autoRecipe.getResultItem(level.registryAccess()).getCount();
            return inputStack.getCount() >= craftingResultCount;
        }

        return false;
    }

    protected void tryDisassembleCurrentItem() {
        // Логика только на сервере
        if (level == null || level.isClientSide) return;

        int slotId = getInputSlot();
        ItemStack inputStack = handler.getStackInSlot(slotId);

        if (inputStack.isEmpty()) return;

        List<ItemStack> results = new ArrayList<>();
        int amountToConsume = 0;
        if (inputStack.getItem() instanceof HandSawItem saw) {
            amountToConsume = 1;
            results.addAll(disassembleHandSaw(inputStack, saw));
        }
        else {
            SimpleContainer tempContainer = new SimpleContainer(1);
            tempContainer.setItem(0, inputStack);
            Optional<DisassemblingRecipe> customRecipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.DISASSEMBLING_TYPE, tempContainer, level);

            if (customRecipe.isPresent()) {
                DisassemblingRecipe recipe = customRecipe.get();
                if (inputStack.getCount() >= recipe.getCountInput()) {
                    amountToConsume = recipe.getCountInput();

                    recipe.getResults().forEach(res -> {
                        if (RANDOM.nextFloat() <= res.chance()) {
                            results.add(res.stack().copy());
                        }
                    });
                }
            }
            else {
                CraftingRecipe autoRecipe = DisassemblyCache.getRecipe(inputStack);

                if (autoRecipe != null) {
                    int craftingResultCount = autoRecipe.getResultItem(level.registryAccess()).getCount();

                    if (inputStack.getCount() >= craftingResultCount) {
                        amountToConsume = craftingResultCount;

                        autoRecipe.getIngredients().forEach(ingredient -> {
                            if (ingredient.isEmpty()) return;
                            if (RANDOM.nextDouble() <= 0.75) {
                                ItemStack[] matchingStacks = ingredient.getItems();
                                if (matchingStacks.length > 0) {
                                    ItemStack returnedItem = matchingStacks[0].copy();
                                    returnedItem.setCount(1);
                                    results.add(returnedItem);
                                }
                            }
                        });
                    }
                }
            }
        }

        if (amountToConsume > 0) {
            handler.extractItem(slotId, amountToConsume, false);
            distributeOutputs(results);
        }
    }

    protected List<ItemStack> disassembleHandSaw(ItemStack stack, HandSawItem sawItem) {
        List<ItemStack> list = new ArrayList<>();

        SawMaterial coreMat = sawItem.getCore(stack);
        SawMaterial teethMat = sawItem.getTeeth(stack);

        if (ModItems.SAW_ITEMS.containsKey(coreMat)) {
            if (RANDOM.nextFloat() <= AUTO_RECIPE_CHANCE){
                list.add(new ItemStack(ModItems.BLADE_ITEMS.get(coreMat).get()));
            }

            ItemStack teeths = new ItemStack(ModItems.TEETH_ITEMS.get(teethMat).get(),
                    (int)(4*RANDOM.nextFloat(0f, 1f)));
            if (!teeths.isEmpty()) list.add(teeths);
        }
        return list;
    }

    protected void distributeOutputs(List<ItemStack> items) {
        for (ItemStack stack : items) {
            ItemStack remaining = stack;
            for (int slot : getOutputSlots()) {
                if (remaining.isEmpty()) break;
                remaining = handler.insertItem(slot, remaining, false);
            }
            if (!remaining.isEmpty()) Block.popResource(Objects.requireNonNull(level), worldPosition, remaining);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return automationLazyHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        internalLazyHandler.invalidate();
        automationLazyHandler.invalidate();
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

}