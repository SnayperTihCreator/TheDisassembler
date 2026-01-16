/**
 * Минимальное количество предметов для начала разборки
 */
function getMinInput() {
    return 1;
}

/**
 * Главная функция разборки пилы
 * @param stack - ItemStackJSWrapper (наша обертка)
 * @param luck - float (бонус удачи от игрока/инструмента)
 * @param random - java.util.Random
 */
function disassemble(stack, luck, random) {
    var output = [];

    // 1. Получаем названия материалов (например, "iron", "diamond", "netherite")
    // В Java это SawMaterial.getName()
    var coreMatName = stack.getSawCore().toLowerCase();
    var teethMatName = stack.getSawTeeth().toLowerCase();

    // Если материала нет, выходим
    if (coreMatName === "none" || teethMatName === "none") {
        return output;
    }

    // 2. Рассчитываем итоговый шанс
    // Учитываем износ: если пила сломана на 50%, шанс на возврат ресурсов падает вдвое.
    var baseChance = 0.75;
    var durabilityFactor = stack.getDurabilityFactor();
    var finalChance = Math.min(1.0, (baseChance + luck) * durabilityFactor);

    // 3. Разборка ОСНОВЫ (Blade)
    // Твоя регистрация в Java: ITEMS.register("%s_blade"...)
    if (random.nextFloat() <= finalChance) {
        output.push({
            id: "thedisassembler:" + coreMatName + "_blade",
            count: 1
        });
    }

    // 4. Разборка ЗУБЬЕВ (Teeth)
    // Твоя регистрация в Java: ITEMS.register("%s_teeth"...)
    // Проверяем 4 раза (как в твоем оригинальном IntStream)
    var teethCount = 0;
    for (var i = 0; i < 4; i++) {
        if (random.nextFloat() <= finalChance) {
            teethCount++;
        }
    }

    if (teethCount > 0) {
        output.push({
            id: "thedisassembler:" + teethMatName + "_teeth",
            count: teethCount
        });
    }

    return output;
}