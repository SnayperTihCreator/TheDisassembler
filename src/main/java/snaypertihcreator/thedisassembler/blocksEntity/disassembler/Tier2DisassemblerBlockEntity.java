package snaypertihcreator.thedisassembler.blocksEntity.disassembler;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.blocks.DisassemblerBlock;
import snaypertihcreator.thedisassembler.blocksEntity.ModBlocksEntity;
import snaypertihcreator.thedisassembler.items.disassembler.HandSawItem;
import snaypertihcreator.thedisassembler.menus.Tier2DisassemblerMenu;

import java.util.Objects;
import java.util.stream.IntStream;

public class Tier2DisassemblerBlockEntity extends DisassemblerBlockEntity {

    private int burnTime;
    private int burnDuration;

    public Tier2DisassemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocksEntity.TIER2_DISASSEMBLER_BE.get(), pos, state, 12); // 0 - вход, 1 - топливо, 2 - диск

    }

    // Рабочая часть(не трогать)
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

    // Проверка соответствия слота предмету
    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0) return true; // Вход
        if (slot == 1) return ForgeHooks.getBurnTime(stack, null) > 0; // Топливо
        if (slot == 2) return stack.getItem() instanceof HandSawItem; // Диск
        return true;
    }

    // номер входа предмета
    @Override
    public int getInputSlot() { return 0; }

    // номер выходных слотов
    @Override
    public int[] getOutputSlots() {
        return IntStream.range(3, 12).toArray();
    }

    // проверка слота для установки воронки
    @Override
    protected boolean canAutomationInsert(int slot) {
        return slot < 3;
    }

    // гетер для рендера самой пилы


    @Override
    public ItemStack getRenderSaw() {
        return handler.getStackInSlot(2);
    }

    @Override
    protected float getLuckModifier() {
        ItemStack disk = handler.getStackInSlot(2);
        if (!(disk.getItem() instanceof HandSawItem sawItem)) return 0;
        return sawItem.getLuckModifier(disk);
    }

    // метод при обновлении ячеек
    @Override
    protected void onInventoryChanged(int slot) {
        super.onInventoryChanged(slot);
        if ((slot == 2) && (level != null) && !level.isClientSide)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    // обновление NBT тегов
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    // Рабочая часть(не трогать)
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected boolean serverTickFuel() {
        boolean dirty = false;

        // 1. Сжигаем текущее топливо
        if (this.burnTime > 0) {
            this.burnTime--;
            dirty = true;
        }

        // 2. Если топливо кончилось, но нужно работать - подкидываем новое
        if (this.burnTime <= 0 && canDisassembleCurrentItem() && hasFreeOutputSlot() && hasRequiredTools()) {
            ItemStack fuelStack = handler.getStackInSlot(1);
            if (!fuelStack.isEmpty()) {
                int fuelTime = ForgeHooks.getBurnTime(fuelStack, null);
                if (fuelTime > 0) {
                    this.burnTime = fuelTime;
                    this.burnDuration = fuelTime;

                    ItemStack remainder = fuelStack.getCraftingRemainingItem();
                    fuelStack.shrink(1);
                    if (fuelStack.isEmpty()) handler.setStackInSlot(1, remainder);
                    dirty = true;
                }
            }
        }

        if (dirty) setChanged();
        return this.burnTime > 0;
    }

    @Override
    protected boolean hasRequiredTools() {
        return !handler.getStackInSlot(2).isEmpty();
    }

    @Override
    protected int calculateMaxProgress() {
        ItemStack disk = handler.getStackInSlot(2);
        int speed = 0;
        if (disk.getItem() instanceof HandSawItem sawItem) speed = (int)(200/sawItem.getSpeedModifier(disk));
        return Math.max(20, speed);
    }

    @Override
    protected void onItemDisassembled() {
        ItemStack disk = handler.getStackInSlot(2);
        if (disk.isDamageableItem()) {
            if (disk.hurt(1, Objects.requireNonNull(level).random, null)) {
                disk.shrink(1);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    protected void regressProgress() {
        if (this.progress > 0) this.progress = Math.max(0, this.progress - 2);
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

    // сохранение состояния
    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("burnDuration", burnDuration);
    }

    // загрузка состояния
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        burnTime = nbt.getInt("burnTime");
        burnDuration = nbt.getInt("burnDuration");
    }

    // получение имя для меню
    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu.%s.advanced_block".formatted(TheDisassemblerMod.MODID));
    }

    // Создание меню
    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new Tier2DisassemblerMenu(id, inv, this, this.data);
    }
}