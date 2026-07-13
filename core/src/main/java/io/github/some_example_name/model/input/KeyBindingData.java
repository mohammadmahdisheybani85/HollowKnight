package io.github.some_example_name.model.input;

public class KeyBindingData {
    public String actionName;
    public int keyCode;

    public KeyBindingData() {
    }

    public KeyBindingData(String actionName, int keyCode) {
        this.actionName = actionName;
        this.keyCode = keyCode;
    }
}
