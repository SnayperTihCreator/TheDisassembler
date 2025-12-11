package snaypertihcreator.thedisassember.items;



public class MaterialLang {
    public record Declensions(String feminine, String plural){}

    private String enName;

    private String ruFeminine;
    private String ruPlural;

    public MaterialLang() {
        this.enName = "Unknown";
        this.ruFeminine = "Неиз.";
        this.ruPlural = "Неиз.";
    }

    public MaterialLang en(String name){
        this.enName = name;
        return this;
    }

    public MaterialLang ru(String feminine, String plural){
        this.ruFeminine = feminine;
        this.ruPlural = plural;
        return this;
    }

    public String getEnName() {return this.enName;}
    public Declensions getRuName(){return new Declensions(this.ruFeminine, this.ruPlural);}

}
