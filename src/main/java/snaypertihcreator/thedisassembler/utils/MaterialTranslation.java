package snaypertihcreator.thedisassembler.utils;

import snaypertihcreator.thedisassembler.TheDisassemblerMod;

public enum MaterialTranslation {
    WOOD(new MaterialLang().en("Wood").ru("Деревянная", "Деревянные", "Деревянное")),
    STONE(new MaterialLang().en("Stone").ru("Каменная", "Каменные", "Каменное")),
    IRON(new MaterialLang().en("Iron").ru("Железная", "Железные", "Железное")),
    GOLD(new MaterialLang().en("Gold").ru("Золотая", "Золотые", "Золотое")),
    DIAMOND(new MaterialLang().en("Diamond").ru("Алмазная", "Алмазные", "Алмазное")),
    NETHERITE(new MaterialLang().en("Netherite").ru("Незеритовая", "Незеритовые", "Незеритовое"));

    private final MaterialLang lang;

    MaterialTranslation(MaterialLang lang) {
        this.lang = lang;
    }

    public MaterialLang getLang() { return lang; }

    // Базовый префикс для ключей локализации
    public String getTranslationKey(String materialName, String suffix) {
        return "material.%s.%s.%s".formatted(TheDisassemblerMod.MODID, materialName, suffix);
    }
}
