package snaypertihcreator.thedisassembler.blocksEntity.disassembler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import snaypertihcreator.thedisassembler.recipes.DisassemblingRecipe;
import snaypertihcreator.thedisassembler.recipes.DisassemblyCache;
import snaypertihcreator.thedisassembler.recipes.ModRecipes;

import java.util.*;

public abstract class DisassemblerBlockEntity extends BlockEntity implements MenuProvider {

    protected static final Random RANDOM = new Random();

    protected final ItemStackHandler handler;
    protected final LazyOptional<IItemHandler> internalLazyHandler;
    protected final LazyOptional<IItemHandler> automationLazyHandler;

    protected int progress = 0;
    protected int maxProgress = 100;
    protected final ContainerData data;

    @Nullable
    protected DisassemblingRecipe cachedRecipe;

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

    // Создние Conteiner-даты для общения между антиверстаком и меню
    protected abstract ContainerData createContainerData();
    // Можно ли взаимодествовать со слотом
    protected abstract boolean canAutomationInsert(int slot);
    // Проверить на валидность предмет - нужно в обрбаотчике
    protected abstract boolean isItemValid(int slot, ItemStack stack);
    // Получить номер слота для входа
    public abstract int getInputSlot();
    // получить номера слотов для выхода
    public abstract int[] getOutputSlots();
    // получить модификатор удачи для каждого станка
    protected abstract float getLuckModifier();
    // метод для вызова обновленя инветаря
    protected void onInventoryChanged(int slot){
        if (level != null && !level.isClientSide && slot != getInputSlot()) return;
        updateRecipeCache();
        this.progress = 0;
    }

    // обновления текущего рецепта
    protected void updateRecipeCache(){
        if (level == null || level.isClientSide) return;

        cachedRecipe = null;

        ItemStack inputStack = handler.getStackInSlot(getInputSlot());
        if (inputStack.isEmpty()) return;

        SimpleContainer tempContainer = new SimpleContainer(1);
        tempContainer.setItem(0, inputStack);

        Optional<DisassemblingRecipe> custom = level.getRecipeManager()
                .getRecipeFor(ModRecipes.DISASSEMBLING_TYPE.get(), tempContainer, level);

        cachedRecipe = custom.orElseGet(() -> DisassemblyCache.getRecipe(inputStack));
    }

    // проверка рецепта на возможность разборки
    public boolean canDisassembleCurrentItem() {
        if (level == null) return false;

        ItemStack inputStack = handler.getStackInSlot(getInputSlot());
        if (inputStack.isEmpty()) return false;
        if (cachedRecipe != null){
            return inputStack.getCount() >= cachedRecipe.getCountInput();
        }
        return false;
    }

    // пробуем разобрать предмет
    protected void tryDisassembleCurrentItem() {
        if (level == null || level.isClientSide) return;

        int slotId = getInputSlot();
        ItemStack inputStack = handler.getStackInSlot(slotId);
        if (inputStack.isEmpty()) return;

        List<ItemStack> results = new ArrayList<>();
        int amountToConsume = 0;
        float currentLuck = getLuckModifier();

        if ((cachedRecipe != null) && (inputStack.getCount() >= cachedRecipe.getCountInput())) {
            amountToConsume = cachedRecipe.getCountInput();
            results.addAll(cachedRecipe.assembleOutputs(inputStack, RANDOM, currentLuck));
        }

        if (amountToConsume > 0) {
            handler.extractItem(slotId, amountToConsume, false);
            distributeOutputs(results);
        }
    }

    // проверка на свободные слоты
    protected boolean hasFreeOutputSlot() {
        for (int slot : getOutputSlots()) {
            ItemStack stack = handler.getStackInSlot(slot); // Тут чуть оптимизировано (переменная)
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    // Метод для очистки содержимого
    public void clearContent() {
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
        progress = 0;
        cachedRecipe = null;
        setChanged();
    }

    // перекладывает предмет в слот результата
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
    /** Обработка топлива. Возвращает true, если энергия есть. */
    protected boolean serverTickFuel() {return true;}
    /** Проверка дополнительных инструментов (пилы и т.д.) */
    protected boolean hasRequiredTools() {return true;}
    /** Хук: Автоматическая ли машина? По умолчанию - ДА (для Tier 2 и 3)*/
    protected boolean isAutomatic() {return true;}
    /** Логика обновления внешнего вида блока (LIT) */
    protected void updateBlockState(BlockState state, boolean isWorking, boolean isBurning) {}
    /** Расчет необходимого времени на операцию */
    protected abstract int calculateMaxProgress();
    /** Действие после успешной разборки (например, сломать пилу) */
    protected void onItemDisassembled() {}
    /** Логика отката прогресса при простое */
    protected abstract void regressProgress();
    protected void updateContainerDataTypes() {}



    public static void tick(Level level, BlockPos ignoredPos, BlockState state, DisassemblerBlockEntity entity) {
        if (level.isClientSide) return;

        // 1. Логика поддержания энергии
        boolean isBurning = entity.serverTickFuel();

        // 2. Проверка: есть ли вход, место на выходе и инструменты
        boolean canWork = isBurning
                && entity.canDisassembleCurrentItem()
                && entity.hasFreeOutputSlot()
                && entity.hasRequiredTools();

        // 3. Обновление состояния блока
        entity.updateBlockState(state, canWork, isBurning);

        // 4. Основной процесс
        if (canWork && entity.isAutomatic()) {
            // Расчет скорости
            entity.maxProgress = entity.calculateMaxProgress();
            entity.progress++;
            if (entity.progress >= entity.maxProgress) entity.finishCrafting();
        } else {
            // Откат прогресса, если машина остановилась
            entity.regressProgress();
        }

        // Синхронизация данных контейнера (если нужно)
        entity.updateContainerDataTypes();
    }

    protected void finishCrafting() {
        tryDisassembleCurrentItem();
        onItemDisassembled();
        this.progress = 0;
    }

    public ItemStack getRenderSaw() {
        return ItemStack.EMPTY;
    }

    // Рабочая зона(не трогать)
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return side == null ? internalLazyHandler.cast() : automationLazyHandler.cast();
        return super.getCapability(cap, side);
    }

    // Рабочая зона(не трогать)
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        internalLazyHandler.invalidate();
        automationLazyHandler.invalidate();
    }

    // сохранение состояний
    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", handler.serializeNBT());
        nbt.putInt("progress", progress);
    }

    // загркзка состояний
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        handler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("progress");
    }

    // получение ответа сервера
    @Override
    public void onLoad() {
        super.onLoad();
        updateRecipeCache();
    }
}