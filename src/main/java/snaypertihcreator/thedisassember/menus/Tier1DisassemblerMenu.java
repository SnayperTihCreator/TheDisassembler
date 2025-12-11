package snaypertihcreator.thedisassember.menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import snaypertihcreator.thedisassember.blocks.ModBlocks;
import snaypertihcreator.thedisassember.blocksEntity.Tier1DisassemblerBlockEntity;

import java.util.Objects;

public class Tier1DisassemblerMenu extends AbstractContainerMenu {
    public final Tier1DisassemblerBlockEntity entity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;

    // Клиент
    public Tier1DisassemblerMenu(int containerID, Inventory inventory, FriendlyByteBuf buf){
        this(containerID, inventory, Objects.requireNonNull(inventory.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(2));
    }

    public Tier1DisassemblerMenu(int containerID, Inventory inventory, BlockEntity entity, ContainerData data){
        super(ModMenuTypes.TIER1_DISASSEMBLER_MENU.get(), containerID);
        checkContainerSize(inventory, 4);
        this.entity = (Tier1DisassemblerBlockEntity)entity;
        this.levelAccess = ContainerLevelAccess.create(inventory.player.level(), entity.getBlockPos());

        this.data = data;
        drawPlayerInventory(inventory, 9, 107);
        drawPlayerHotbar(inventory, 9, 165);

        this.entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 51, 43));
            drawGridOutput(handler, 110, 25);
        });

        addDataSlots(data);
    }

    public int getScaledProgress(int arrowSize) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, ModBlocks.BASIC_BLOCK.get());
    }

    private void drawPlayerInventory(Inventory inventory, int x, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + i * 9 + 9,
                        x + l*18, y + i*18));
            }
        }
    }

    private void drawPlayerHotbar(Inventory inventory, int x, int y) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, x + i * 18, y));
        }
    }

    private void drawGridOutput(IItemHandler handler, int x, int y){
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = 1 + (row * 3 + col);

                this.addSlot(new SlotItemHandler(handler, slotIndex,
                        x + (col * 18), // Сдвиг по X на 18 пикселей
                        y + (row * 18)  // Сдвиг по Y на 18 пикселей
                ));
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // КОНСТАНТЫ (Зависят от порядка addSlot)
        final int VANILLA_SLOT_COUNT = 36; // 0-35
        final int MACHINE_INPUT_SLOT = 36; // Слот входа (первый, который мы добавили из handler)
        final int TOTAL_SLOT_COUNT = 46;   // 36 (игрок) + 10 (машина)

        // 1. ЕСЛИ КЛИКНУЛИ ПО МАШИНЕ (Вход 36 или Выходы 37-45) -> ПЕРЕНОСИМ ИГРОКУ
        if (index >= VANILLA_SLOT_COUNT && index < TOTAL_SLOT_COUNT) {
            // Пытаемся засунуть в инвентарь игрока (0 - 36)
            if (!moveItemStackTo(sourceStack, 0, VANILLA_SLOT_COUNT, true)) {
                return ItemStack.EMPTY;
            }
        }

        // 2. ЕСЛИ КЛИКНУЛИ ПО ИНВЕНТАРЮ ИГРОКА -> ПЕРЕНОСИМ В МАШИНУ
        else if (index < VANILLA_SLOT_COUNT) {
            // Пытаемся засунуть ТОЛЬКО во Входной слот (36).
            // В выходы (37+) мы не хотим, чтобы игрок мог засунуть предметы шифт-кликом.
            if (!moveItemStackTo(sourceStack, MACHINE_INPUT_SLOT, MACHINE_INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        // Стандартная проверка: если стак опустел, очищаем слот
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

}
