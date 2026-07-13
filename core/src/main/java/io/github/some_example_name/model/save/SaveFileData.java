package io.github.some_example_name.model.save;

import java.util.ArrayList;
import java.util.List;

public class SaveFileData {
    public int lastPlayedSlotNumber = 1;
    public List<SaveSlotData> slots = new ArrayList<>();

    public SaveFileData() {
    }
}
