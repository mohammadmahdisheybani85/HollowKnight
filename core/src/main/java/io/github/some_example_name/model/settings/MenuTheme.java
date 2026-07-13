package io.github.some_example_name.model.settings;

public enum MenuTheme {
    CLASSIC,
    VOID,
    VERDANT;

    public MenuTheme next() {
        MenuTheme[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
