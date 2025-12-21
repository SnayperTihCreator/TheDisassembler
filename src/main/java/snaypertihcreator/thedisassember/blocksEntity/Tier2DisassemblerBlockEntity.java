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
import snaypertihcreator.thedisassember.blocks.DisassemberBlock;
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

    // Проверка соответсвия слота предмету
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
    public ItemStack getRenderSaw(){
        return handler.getStackInSlot(2);
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

    // проверка процесса плавки
    private boolean isBurning() {
        return this.burnTime > 0;
    }

    //метод процесса плавки
    public static void tick(@NotNull Level level, BlockPos pos, BlockState state, Tier2DisassemblerBlockEntity entity) {
        if (level.isClientSide) return;

        boolean wasLit = state.getValue(DisassemberBlock.LIT); // Горит ли текстура сейчас?
        boolean wasActive = entity.isActive;                   // Работала ли машина в прошлом тике?
        boolean dirty = false;                                 // Нужно ли сохранять NBT?

        // --- ЛОГИКА СЖИГАНИЯ ТОПЛИВА ---
        if (entity.isBurning()) {
            entity.burnTime--;
            dirty = true;
        }

        ItemStack fuel = entity.handler.getStackInSlot(1);
        ItemStack disk = entity.handler.getStackInSlot(2);

        boolean hasInputAndDisk = entity.canDisassembleCurrentItem() && !disk.isEmpty();

        // Попытка зажечь новое топливо, если старое кончилось
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

        boolean shouldBeLit = entity.isBurning();

        // если уже не горит
        if (wasLit != shouldBeLit) {
            level.setBlock(pos, state.setValue(DisassemberBlock.LIT, shouldBeLit), 3);
            dirty = true;
        }

        // если не активно
        if (wasActive != entity.isActive) {
            dirty = true;
            level.sendBlockUpdated(pos, state, state, 3);
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }


    // сохранение состояния
    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("burnTime", burnTime);
        nbt.putInt("burnDuration", burnDuration);
        nbt.putBoolean("isActive", isActive);
    }

    // загрузка состояния
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        burnTime = nbt.getInt("burnTime");
        burnDuration = nbt.getInt("burnDuration");
        isActive = nbt.getBoolean("isActive");
    }

    // получение имя для меню
    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("menu." + TheDisassemberMod.MODID + ".advanced_block");
    }

    // Создание меню
    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new Tier2DisassemblerMenu(id, inv, this, this.data);
    }
}