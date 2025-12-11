package snaypertihcreator.thedisassember.items;

public enum TierSaw {

    WOOD("wood", 1),
    STONE("stone", 2),
    IRON("iron", 3),
    GOLD("gold", 2),
    DIAMOND("diamond", 4),
    NETHERITE("netherite", 5);

    private final String name;
    private final int level;

    TierSaw(String name, int level){
        this.name = name;
        this.level = level;
    }

    public String getName(){return this.name;}

    public int getMaxUses(){
        int baseDurability = 32;
        return baseDurability * (int)Math.pow(2, this.level - 1);
    }
}
