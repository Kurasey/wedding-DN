package io.github.kurasey.wedding_invitation.model;

import java.util.Arrays;

public enum Beverage {
    DRY_WHITE_WINE("Белое сухое вино"),
    SEMI_SWEET_RED_WINE("Красное полусладкое вино"),
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