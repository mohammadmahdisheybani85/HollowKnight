package io.github.some_example_name.model.input;

import java.util.ArrayList;
import java.util.List;

public class KeyBindingFileData {
    public int version;
    public List<KeyBindingData> keyBindings = new ArrayList<>();

    public KeyBindingFileData() {
    }
}
