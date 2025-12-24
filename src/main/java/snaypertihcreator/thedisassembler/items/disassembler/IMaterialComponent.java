package snaypertihcreator.thedisassembler.items.disassembler;

import net.minecraft.network.chat.Component;
import snaypertihcreator.thedisassembler.TheDisassemblerMod;
import snaypertihcreator.thedisassembler.utils.WordGender;

public interface IMaterialComponent {
    WordGender getGender();
    SawMaterial getMaterial();

    default Component getMaterialName() {
        String matName = getMaterial().getName();
        String genderSuffix = switch (getGender()) {
            case FEMININE -> "adj";     // .adj -> Железная
            case PLURAL -> "plural";   // .plural -> Железные
            case NEUTRAL -> "neutral"; // .neutral -> Железное
        };

        String key = "material.%s.%s.%s".formatted(TheDisassemblerMod.MODID, matName, genderSuffix);
        return Component.translatable(key);
    }
}
