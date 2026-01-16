/**
 * Карта соответствия десятичных цветов Minecraft и ID красителей
 */
var COLOR_MAP = {
    1973019:  "minecraft:black_dye",
    11743532: "minecraft:red_dye",
    3887386:  "minecraft:green_dye",
    5320730:  "minecraft:brown_dye",
    2437522:  "minecraft:blue_dye",
    8073150:  "minecraft:purple_dye",
    2654032:  "minecraft:cyan_dye",
    11250603: "minecraft:light_gray_dye",
    4408131:  "minecraft:gray_dye",
    14188952: "minecraft:pink_dye",
    4312372:  "minecraft:lime_dye",
    14602026: "minecraft:yellow_dye",
    6719955:  "minecraft:light_blue_dye",
    12801229: "minecraft:magenta_dye",
    15435844: "minecraft:orange_dye",
    15790320: "minecraft:white_dye"
};

/**
 * Вспомогательная функция для определения красителя по цвету
 */
function getDyeFromColor(colorInt) {
    return COLOR_MAP[colorInt] || "minecraft:white_dye";
}

function getMinInput() {
    return 3;
}

/**
 * Основная логика разборки
 */
function disassemble(stack, luck, random) {
    var output = [];
    var chance = 0.75 + luck; // Базовый шанс (можно подкрутить)

    // 1. БУМАГА (Основа)
    output.push({ id: "minecraft:paper", count: 1 });

    // 2. ПОРОХ (Зависит от длительности полета)
    var flight = stack.getInt("Fireworks.Flight");
    if (flight < 1) flight = 1; // Минимум 1 порох всегда в крафте
    for (var i = 0; i < flight; i++) {
        if (random.nextFloat() < chance) {
            output.push({ id: "minecraft:gunpowder", count: 1 });
        }
    }

    // 3. ВЗРЫВЫ (Explosions)
    var explosionCount = stack.getListSize("Fireworks.Explosions");
    for (var j = 0; j < explosionCount; j++) {
        var path = "Fireworks.Explosions." + j;

        // Возвращаем саму звездочку
        if (random.nextFloat() < chance) {
            output.push({ id: "minecraft:firework_star", count: 1 });
        }

        // --- Разбор эффектов звездочки ---

        // Мерцание (Flicker) -> Светопыль
        if (stack.getInt(path + ".Flicker") === 1) {
            if (random.nextFloat() < chance) output.push({ id: "minecraft:glowstone_dust", count: 1 });
        }

        // След (Trail) -> Алмаз
        if (stack.getInt(path + ".Trail") === 1) {
            if (random.nextFloat() < chance) output.push({ id: "minecraft:diamond", count: 1 });
        }

        // Форма взрыва (Type) -> Специфический предмет
        var type = stack.getInt(path + ".Type");
        if (type > 0) {
            var shapeItems = {
                1: "minecraft:feather",       // Клок
                2: "minecraft:gold_nugget",   // Звезда
                3: "minecraft:skeleton_skull",// Крипер (упрощено)
                4: "minecraft:fire_charge"    // Крупный шар
            };
            if (shapeItems[type] && random.nextFloat() < chance) {
                output.push({ id: shapeItems[type], count: 1 });
            }
        }

        // --- Разбор цветов (Красители) ---
        var colors = stack.getIntArray(path + ".Colors");
        if (colors) {
            for (var k = 0; k < colors.length; k++) {
                // Шанс на красители чуть ниже, чтобы не заваливать ресурсами
                if (random.nextFloat() < (chance * 0.6)) {
                    output.push({ id: getDyeFromColor(colors[k]), count: 1 });
                }
            }
        }

        // Дополнительные цвета при затухании (FadeColors)
        var fadeColors = stack.getIntArray(path + ".FadeColors");
        if (fadeColors) {
            for (var f = 0; f < fadeColors.length; f++) {
                if (random.nextFloat() < (chance * 0.3)) {
                    output.push({ id: getDyeFromColor(fadeColors[f]), count: 1 });
                }
            }
        }
    }

    return output;
}