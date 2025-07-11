package io.github.kaurami.wems.model;

import java.util.Arrays;

public enum Beverage {
    DRY_WINE("Cухое вино"),
    SEMI_SWEET_WINE("Полусладкое вино"),
    COGNAC("Коньяк"),
    VODKA("Водка"),
    CHAMPAGNE("Шампанское"),
    NON_ALCOHOLIC("Безалкогольные напитки");

    private final String displayName;

    Beverage(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return displayName;
    }

    public static Beverage fromDisplayName(String text) {
        return Arrays.stream(values())
                .filter(b -> b.displayName.equalsIgnoreCase(text))
                .findFirst()
                .orElse(NON_ALCOHOLIC);
    }
}