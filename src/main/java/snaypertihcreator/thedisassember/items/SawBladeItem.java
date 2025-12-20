package snaypertihcreator.thedisassember.items;

import net.minecraft.world.item.Item;

//Класс компонент серцевина
public class SawBladeItem extends Item {
    private final SawMaterial material;

    public SawBladeItem(SawMaterial material) {
        super(new Properties());
        this.material = material;
    }

    public SawMaterial getMaterial() {return material;}
}
