package snaypertihcreator.thedisassember;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ModCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_ITEMS;

    static {
        BUILDER.push("Common config from The Disassembler");

        EXCLUDED_ITEMS = BUILDER.comment("List of items to ignore during auto-disassembly discovery.\n" +
                "You can specify Item IDs (e.g. 'minecraft:bedrock') or Tags (start with #, e.g. '#minecraft:wool').")
                .defineList("excluded_items", List.of(
                        "#minecraft:trim_templates",
                        "minecraft:netherite_upgrade_smithing_template" // Исключения предметов
                ), entry -> true);
        BUILDER.pop();

        SPEC = BUILDER.build();

    }
}
