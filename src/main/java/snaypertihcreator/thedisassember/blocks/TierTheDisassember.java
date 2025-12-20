package snaypertihcreator.thedisassember.blocks;

// уровни антиверстака
public enum TierTheDisassember {
    BASIC(1),
    ADVANCED(2);
    //TODO добавить третий уровень


    private final int level;

    TierTheDisassember(int level){
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
