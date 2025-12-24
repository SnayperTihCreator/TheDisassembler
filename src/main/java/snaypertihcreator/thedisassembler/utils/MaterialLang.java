package snaypertihcreator.thedisassembler.utils;

public class MaterialLang {
    // Храним данные прямо в рекордах
    private String enName = "Unknown";
    private Declensions ru = new Declensions("Неизв.", "Неизв.", "Неизв.");

    public record Declensions(String feminine, String plural, String neutral) {}

    public MaterialLang en(String name) {
        this.enName = name;
        return this;
    }

    public MaterialLang ru(String fem, String plur, String neut) {
        this.ru = new Declensions(fem, plur, neut);
        return this;
    }

    public String getEnName() { return enName; }
    public Declensions getRuName() { return ru; }
}