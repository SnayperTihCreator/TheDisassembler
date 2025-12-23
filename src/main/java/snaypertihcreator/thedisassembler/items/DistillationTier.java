package snaypertihcreator.thedisassembler.items;

import net.minecraft.world.item.Rarity;

public enum DistillationTier {
    GLASS(0.50f, 16, Rarity.COMMON, false),
    REINFORCED(0.75f, 64, Rarity.UNCOMMON, false),
    INDUSTRIAL(1.00f, 512, Rarity.RARE, true); // true = несгораемый

    private final float efficiency;
    private final int durability;
    private final Rarity rarity;
    private final boolean fireResistant;

    DistillationTier(float efficiency, int durability, Rarity rarity, boolean fireResistant) {
        this.efficiency = efficiency;
        this.durability = durability;
        this.rarity = rarity;
        this.fireResistant = fireResistant;
    }

    public float getEfficiency() {
        return efficiency;
    }

    public int getDurability() {
        return durability;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public boolean isFireResistant() {
        return fireResistant;
    }
}