package snaypertihcreator.thedisassember.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassember.TheDisassemberMod;
import snaypertihcreator.thedisassember.items.HandSawItem;
import snaypertihcreator.thedisassember.menus.Tier1DisassemblerMenu;
import snaypertihcreator.thedisassember.recipes.DisassemblingRecipe;
import snaypertihcreator.thedisassember.recipes.DisassemblyCache;
import snaypertihcreator.thedisassember.recipes.ModRecipes;

import java.util.Optional;
import java.util.stream.IntStream;

public class Tier1DisassemblerBlockEntity extends DisassemblerBlockEntity {

    private int isValidRecipe = 0;

    public Tier1DisassemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocksEntity.TIER1_DISASSEMBER_BE.get(), pos, state, 10);
    }

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> Tier1DisassemblerBlockEntity.this.progress;
                    case 1 -> Tier1DisassemblerBlockEntity.this.maxProgress;
                    case 2 -> Tier1DisassemblerBlockEntity.this.isValidRecipe;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> Tier1DisassemblerBlockEntity.this.progress = value;
                    case 1 -> Tier1DisassemblerBlockEntity.this.maxProgress = value;
                    case 2 -> Tier1DisassemblerBlockEntity.this.isValidRecipe = value;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    protected void onInventoryChanged(int slot) {
        checkRecipeValidity();
    }

    @Override
    public int getInputSlot() {
        return 0;
    }

    @Override
    public int[] getOutputSlots() {
        return IntStream.range(1, 10).toArray();
    }

    public void spined() {
        if (level == null || level.isClientSide) return;

        checkRecipeValidity();

        if (this.isValidRecipe == 0 || !hasFreeOutputSlot()) return;
        this.progress += 5;
        if (this.progress >= this.maxProgress) {
            tryDisassembleCurrentItem();
            this.progress = 0;
        }
        setChanged();
    }

    private void checkRecipeValidity() {
        if (level == null) return;
        ItemStack inputStack = handler.getStackInSlot(0);

        if (inputStack.isEmpty()) {
            this.isValidRecipe = 0;
            this.progress = 0;
            return;
        }

        // 1. Проверка на пилу
        if (inputStack.getItem() instanceof HandSawItem) {
            this.isValidRecipe = 1;
            return;
        }

        // 2. Проверка кастомных рецептов
        SimpleContainer tempInv = new SimpleContainer(1);
        tempInv.setItem(0, inputStack);
        Optional<DisassemblingRecipe> disassembleRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.DISASSEMBLING_TYPE, tempInv, level);

        if (disassembleRecipe.isPresent()) {
            this.isValidRecipe = 1;
            return;
        }

        // 3. Проверка ванильных рецептов через кэш
        CraftingRecipe autoRecipe = DisassemblyCache.getRecipe(inputStack);
        if (autoRecipe != null) {
            int requiredCount = autoRecipe.getResultItem(level.registryAccess()).getCount();
            if (inputStack.getCount() >= requiredCount) {
                this.isValidRecipe = 1;
                return;
            }
        }

        this.isValidRecipe = 0;
    }

    private boolean hasFreeOutputSlot() {
        for (int slot : getOutputSlots()) {
            if (handler.getStackInSlot(slot).isEmpty() ||
                    (handler.getStackInSlot(slot).getCount() < handler.getStackInSlot(slot).getMaxStackSize())) {
                return true;
            }
        }
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, Tier1DisassemblerBlockEntity entity) {
        if (level.isClientSide()) return;

        if (entity.progress > 0) {
            entity.progress--;
            setChanged(level, pos, state);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        checkRecipeValidity();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu." + TheDisassemberMod.MODID + ".base_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerID, @NotNull Inventory inventory, @NotNull Player player) {
        return new Tier1DisassemblerMenu(containerID, inventory, this, this.data);
    }
}