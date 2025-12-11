package snaypertihcreator.thedisassember.items;

import net.minecraft.world.item.Item;

public class SawItem extends Item {
    private final MaterialType material;
    private final TierSaw tier;

    public SawItem(MaterialType material, TierSaw tier) {
        super(new Properties().durability(tier.getMaxUses()));
        this.tier = tier;
        this.material = material;
    }

    public MaterialType getMaterial() {return material;}

    public TierSaw getTier(){return tier;}
}
