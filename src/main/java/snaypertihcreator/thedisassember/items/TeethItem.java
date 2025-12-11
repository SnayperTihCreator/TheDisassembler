package snaypertihcreator.thedisassember.items;

import net.minecraft.world.item.Item;

public class TeethItem extends Item {
    private final MaterialType material;

    public TeethItem(MaterialType material) {
        super(new Properties());
        this.material = material;
    }

    public MaterialType getMaterial() {
        return material;
    }
}
