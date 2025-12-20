package snaypertihcreator.thedisassember.blocks;

// уровни разборщиков
public enum TierTheDisassember {
    BASIC(1),
    ADVANCED(2);


    private final int level;

    TierTheDisassember(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
