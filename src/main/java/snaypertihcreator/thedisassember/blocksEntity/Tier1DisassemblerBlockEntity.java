package snaypertihcreator.thedisassember.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import snaypertihcreator.thedisassember.recipes.DisassemblingRecipe;
import snaypertihcreator.thedisassember.recipes.DisassemblyCache;
import snaypertihcreator.thedisassember.recipes.ModRecipes;

import java.util.*;

public class Tier1DisassemblerBlockEntity extends BlockEntity implements MenuProvider {

    private static final double AUTO_RECIPE_CHANCE = 0.75;
    private static final Random RANDOM = new Random();

    private final SimpleContainer inventoryWrapper = new SimpleContainer(1);

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
        if (level == null || level.isClientSide) return;

        ItemStack inputStack = handler.getStackInSlot(0);

        if (inputStack.isEmpty()) return;

        inventoryWrapper.setItem(0, inputStack);
        Optional<DisassemblingRecipe> disassembleRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.DISASSEMBLING_TYPE, inventoryWrapper, level);

        CraftingRecipe autoRecipe = DisassemblyCache.getRecipe(inputStack);

        if (disassembleRecipe.isEmpty() && autoRecipe == null) return;

        if (disassembleRecipe.isEmpty() && autoRecipe != null){
            int requiredCount = autoRecipe.getResultItem(level.registryAccess()).getCount();
            if (inputStack.getCount() < requiredCount) return;
        }

        if (!hasFreeSlot()) return;

        this.progress += 5;
        if (this.progress >= this.maxProgress){
            if (disassembleRecipe.isPresent()) craftDisassembleRecipe(disassembleRecipe.get());
            else craftAutoRecipe(autoRecipe);
            this.progress = 0;
        }
        setChanged();
    }

    private void craftDisassembleRecipe(DisassemblingRecipe recipe){
        handler.extractItem(0, recipe.getCountInput(), false);
        List<ItemStack> resultsToGive = new ArrayList<>();

        recipe.getResults().forEach(element -> {
            if (RANDOM.nextFloat() <= element.chance()) resultsToGive.add(element.stack().copy());
        });
        insertOutputItems(resultsToGive);
    }

    private void craftAutoRecipe(CraftingRecipe recipe){
        if (recipe == null) return;
        int countToExtract = recipe.getResultItem(Objects.requireNonNull(level).registryAccess()).getCount();

        handler.extractItem(0, countToExtract, false);
        List<ItemStack> resultsToGive = new ArrayList<>();
        List<Ingredient> ingredients = recipe.getIngredients();

        ingredients.forEach(element -> {
            if (element.isEmpty()) return;
            if (RANDOM.nextDouble() <= AUTO_RECIPE_CHANCE){
                ItemStack[] matchingStacks = element.getItems();
                if (matchingStacks.length > 0) {
                    ItemStack stackToAdd = matchingStacks[0].copy();
                    stackToAdd.setCount(1);
                    resultsToGive.add(stackToAdd);
                }
            }
        });
        insertOutputItems(resultsToGive);
    }

    private void insertOutputItems(List<ItemStack> items) {
        for (ItemStack resultStack : items) {
            ItemStack remaining = resultStack;
            for (int i = 1; i < handler.getSlots(); i++) {
                if (remaining.isEmpty()) break;
                remaining = handler.insertItem(i, remaining, false);
            }
            if (!remaining.isEmpty()) {

                Block.popResource(Objects.requireNonNull(level), worldPosition, remaining);
            }
        }
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

    public static void tick(Level level, BlockPos pos, BlockState state, Tier1DisassemblerBlockEntity entity) {
        if (level.isClientSide()) return;
        if (entity.progress > 0) {
            entity.progress--;
            setChanged(level, pos, state);
        }
    }

    private boolean hasFreeSlot() {
        for (int i = 1; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
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
