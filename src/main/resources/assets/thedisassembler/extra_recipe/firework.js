function disassemble(stack) {
    let drops = [{id: "minecraft:paper", count: 1}];
    let nbt = stack.tag || {};
    let fw = nbt.Fireworks || {};

    // Flight duration
    let flight = fw.Flight || 0;
    drops.push({id: "minecraft:gunpowder", count: Math.max(1, flight + 1)});

    // Explosions
    let explosions = fw.Explosions || [];
    explosions.forEach(exp => {
        drops.push({id: "minecraft:gunpowder", count: 1});

        if (exp.Colors) {
            exp.Colors.forEach(color => {
                drops.push({id: colorToDye(color), count: 1});
            });
        }

        let shapes = {
            1: "minecraft:fire_charge",
            2: "minecraft:gold_nugget",
            4: "minecraft:feather"
        };
        if (shapes[exp.Type]) {
            drops.push({id: shapes[exp.Type], count: 1});
        }

        if (exp.Trail) drops.push({id: "minecraft:diamond", count: 1});
        if (exp.Flicker) drops.push({id: "minecraft:glowstone_dust", count: 1});
    });

    return drops;
}

function colorToDye(colorInt) {
    let colors = {
        16711680: "minecraft:red_dye",
        65280: "minecraft:lime_dye",
        255: "minecraft:yellow_dye",
        16711935: "minecraft:orange_dye"
    };
    return colors[colorInt] || "minecraft:white_dye";
}
