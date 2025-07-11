package io.github.kaurami.wems.model;

public enum ActionSource {
    ADMIN("Администратор"),
    GUEST("Гость");

    private final String displayName;

    ActionSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}