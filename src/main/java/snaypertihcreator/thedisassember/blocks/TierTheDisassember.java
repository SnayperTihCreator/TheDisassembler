package snaypertihcreator.thedisassember.blocks;

public enum TierTheDisassember {
    BASIC(1);


    private final int level;

    TierTheDisassember(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
