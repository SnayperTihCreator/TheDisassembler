package snaypertihcreator.thedisassembler.blocksEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.DisassemblerBlock;
import snaypertihcreator.thedisassembler.energy.ModEnergyStorage;
import snaypertihcreator.thedisassembler.items.HandSawItem;
import snaypertihcreator.thedisassembler.menus.Tier3DisassemblerMenu;
// import snaypertihcreator.thedisassembler.menus.Tier3DisassemblerMenu; // Вам нужно создать это меню

import java.util.Objects;
import java.util.stream.IntStream;

public class Tier3DisassemblerBlockEntity extends DisassemblerBlockEntity {

    // Настройки баланса
    public static final int ENERGY_CAPACITY = 60000;
    public static final int ENERGY_RECEIVE = 256;
    public static final int ENERGY_PER_TICK = 40; // Сколько энергии жрет за тик работы

    private final ModEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyOptional;

    public Tier3DisassemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocksEntity.TIER3_DISASSEMBLER_BE.get(), pos, state, 11); // 1 вход, 1 пила, 9 выход
        this.energyStorage = new ModEnergyStorage(ENERGY_CAPACITY, ENERGY_RECEIVE);
        this.energyOptional = LazyOptional.of(() -> this.energyStorage);
    }

    // --- НАСТРОЙКА СЛОТОВ ---

    @Override
    public int getInputSlot() { return 0; }

    @Override
    public ItemStack getRenderSaw() {
        return handler.getStackInSlot(1);
    }

    @Override
    protected float getLuckModifier() {
        ItemStack disk = handler.getStackInSlot(1);
        if (!(disk.getItem() instanceof HandSawItem sawItem)) return 0;
        return sawItem.getLuckModifier(disk);
    }

    @Override
    public int[] getOutputSlots() {
        return IntStream.range(2, 11).toArray(); // Слоты 2-10
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0) return true; // Вход
        if (slot == 1) return stack.getItem() instanceof HandSawItem; // Пила
        return false; // В выход и другие ничего класть нельзя руками
    }

    @Override
    protected boolean canAutomationInsert(int slot) {
        return slot < 2; // Автоматизация может класть
    }

    @Override
    protected boolean serverTickFuel() {
        if (this.energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
            // Если условия работы соблюдены (проверяем их тут, чтобы не тратить энергию впустую)
            if (canDisassembleCurrentItem() && hasFreeOutputSlot() && hasRequiredTools()) {
                this.energyStorage.consumeEnergy(ENERGY_PER_TICK);
                return true; // Энергия есть и потребляется
            }
        }
        return false; // Энергии нет или работать не надо
    }

    @Override
    protected boolean hasRequiredTools() {
        return !handler.getStackInSlot(1).isEmpty();
    }

    @Override
    protected int calculateMaxProgress() {
        ItemStack sawStack = handler.getStackInSlot(1);
        int speedModifier = 0;
        if (sawStack.getItem() instanceof HandSawItem sawItem) speedModifier = sawItem.getToolLevel(sawStack);
        return Math.max(10, 100 - (speedModifier * 20));
    }

    @Override
    protected void onItemDisassembled() {
        // Ломаем пилу
        ItemStack sawStack = handler.getStackInSlot(1);
        if (sawStack.isDamageableItem() && sawStack.hurt(1, Objects.requireNonNull(level).random, null)) sawStack.shrink(1);
    }

    @Override
    protected void updateBlockState(BlockState state, boolean isWorking, boolean isBurning) {
        boolean dirty = false;

        // 1. Проверяем состояние LIT (Горит)
        boolean currentLit = state.getValue(DisassemblerBlock.LIT);
        if (currentLit != isBurning) {
            state = state.setValue(DisassemblerBlock.LIT, isBurning);
            dirty = true;
        }

        // 2. Проверяем состояние WORKING (Пилит)
        boolean currentWorking = state.getValue(DisassemblerBlock.WORKING);
        if (currentWorking != isWorking) {
            state = state.setValue(DisassemblerBlock.WORKING, isWorking);
            dirty = true;
        }

        // Если хоть что-то изменилось - обновляем блок в мире
        if (dirty) {
            Objects.requireNonNull(level).setBlock(worldPosition, state, 3);
            setChanged();
        }
    }

    @Override
    protected void regressProgress() {
        // Ускоренный откат (как было в вашем коде)
        if (this.progress > 0) this.progress = Math.max(0, this.progress - 2);
    }

    // --- ЭНЕРГИЯ И КАПАБИЛИТИ ---

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return energyOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
    }

    // --- ДАННЫЕ КОНТЕЙНЕРА (Sync) ---

    @Override
    protected ContainerData createContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> Tier3DisassemblerBlockEntity.this.progress;
                    case 1 -> Tier3DisassemblerBlockEntity.this.maxProgress;
                    case 2 -> Tier3DisassemblerBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> Tier3DisassemblerBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> Tier3DisassemblerBlockEntity.this.progress = value;
                    case 1 -> Tier3DisassemblerBlockEntity.this.maxProgress = value;
                    case 2 -> Tier3DisassemblerBlockEntity.this.energyStorage.setEnergy(value);
                }
            }

            @Override
            public int getCount() { return 4; }
        };
    }
    // --- СОХРАНЕНИЕ / ЗАГРУЗКА ---

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("Energy")) energyStorage.deserializeNBT(nbt.get("Energy"));
    }

    // --- МЕНЮ ---

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu." + TheDisassemblerMod.MODID + ".progressive_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {

        return new Tier3DisassemblerMenu(id, inv, this, this.data);

    }
}