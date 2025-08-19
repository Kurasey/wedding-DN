package io.github.kaurami.wems.model;

public enum TransferOption {
    NOT_REQUIRED("Нет"),
    TO_VENUE_ONLY("Только до торжества"),
    FROM_VENUE_ONLY("Только после торжества"),
    BOTH_WAYS("До и после торжества");

    private final String displayName;

    TransferOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}