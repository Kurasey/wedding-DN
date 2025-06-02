package io.github.kurasey.wedding_invitation.model;

public enum Beverage {
    DRY_WET_WINE("Белое сухое вино"), SEMI_SWEET_RED_WINE ("Красное полуслакое вино"), COGNAC ("Коньяк"),
    VODKA ("Водка"), SOFT_DRINKS ("Безалкогольные напитки");

    private final String name;

    Beverage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
