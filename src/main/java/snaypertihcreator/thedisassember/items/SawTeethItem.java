package snaypertihcreator.thedisassember.items;

import net.minecraft.world.item.Item;

// Класс компонент - зубье пилы
public class SawTeethItem extends Item {
    private final SawMaterial material;

    public SawTeethItem(SawMaterial material) {
        super(new Properties());
        this.material = material;
    }

    public SawMaterial getMaterial() {return material;}
}
