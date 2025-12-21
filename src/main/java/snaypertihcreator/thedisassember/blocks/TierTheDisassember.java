package snaypertihcreator.thedisassember.blocks;

// уровни антиверстака
public enum TierTheDisassember {
    BASIC(1),
    ADVANCED(2),
    PROGRESSIVE(3);


    private final int level;

    TierTheDisassember(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
