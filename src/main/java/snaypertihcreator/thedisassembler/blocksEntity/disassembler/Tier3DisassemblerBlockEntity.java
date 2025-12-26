package snaypertihcreator.thedisassembler.blocksEntity.disassembler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.DisassemblerBlock;
import snaypertihcreator.thedisassembler.blocksEntity.ModBlocksEntity;
import snaypertihcreator.thedisassembler.energy.ModEnergyStorage;
import snaypertihcreator.thedisassembler.items.disassembler.HandSawItem;
import snaypertihcreator.thedisassembler.menus.disassembler.Tier3DisassemblerMenu;
// import snaypertihcreator.thedisassembler.menus.disassembler.Tier3DisassemblerMenu; // Вам нужно создать это меню

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
    protected void onInventoryChanged(int slot) {
        super.onInventoryChanged(slot);
        if ((slot == 1) && (level != null) && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

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
        return true; // В выход и другие ничего класть нельзя руками
    }

    @Override
    protected boolean canAutomationInsert(int slot) {
        return slot < 2; // Автоматизация может класть
    }

    @Override
    protected boolean serverTickFuel() {
        if (this.energyStorage.getEnergyStored() >= ENERGY_PER_TICK) {
            if (canDisassembleCurrentItem() && hasFreeOutputSlot() && hasRequiredTools()) {
                this.energyStorage.consumeEnergy(ENERGY_PER_TICK);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean hasRequiredTools() {
        return !handler.getStackInSlot(1).isEmpty();
    }

    @Override
    protected int calculateMaxProgress() {
        ItemStack disk = handler.getStackInSlot(1);
        int speed = 0;
        if (disk.getItem() instanceof HandSawItem sawItem) speed = (int)(200/sawItem.getSpeedModifier(disk));
        return Math.max(20, speed);
    }

    @Override
    protected void onItemDisassembled() {
        ItemStack sawStack = handler.getStackInSlot(1);
        if (sawStack.isDamageableItem()) {
            if (sawStack.hurt(1, Objects.requireNonNull(level).random, null)) {
                sawStack.shrink(1);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    protected void updateBlockState(BlockState state, boolean isWorking, boolean isBurning) {
        boolean currentLit = state.getValue(DisassemblerBlock.LIT);
        boolean currentWorking = state.getValue(DisassemblerBlock.WORKING);

        if (currentLit != isBurning || currentWorking != isWorking) {
            state = state.setValue(DisassemblerBlock.LIT, isBurning)
                    .setValue(DisassemblerBlock.WORKING, isWorking);
            Objects.requireNonNull(level).setBlock(worldPosition, state, 3);
            setChanged();
        }
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
        return Component.translatable("menu.%s.progressive_disassembler".formatted(TheDisassemblerMod.MODID));
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new Tier3DisassemblerMenu(id, inv, this, this.data);
    }
}