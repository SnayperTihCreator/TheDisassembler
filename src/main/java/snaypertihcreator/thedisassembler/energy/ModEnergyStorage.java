package snaypertihcreator.thedisassembler.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class ModEnergyStorage extends EnergyStorage implements INBTSerializable<Tag> {

    public ModEnergyStorage(int capacity, int maxReceive) {
        super(capacity, maxReceive, 0); // 0 maxExtract означает, что трубы не могут выкачивать энергию ИЗ машины
    }

    // Метод, чтобы сама машина могла тратить энергию на работу
    public void consumeEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }

    // Метод для установки энергии (нужен для синхронизации с клиентом)
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    // Сохранение энергии при перезаходе в мир (NBT)
    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", this.energy);
        return tag;
    }

    // Загрузка энергии
    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag tag) {
            this.energy = tag.getInt("energy");
        }
    }
}