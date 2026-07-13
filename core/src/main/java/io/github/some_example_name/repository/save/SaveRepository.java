package io.github.some_example_name.repository.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.some_example_name.model.save.SaveFileData;
import io.github.some_example_name.model.save.SaveSlot;
import io.github.some_example_name.model.save.SaveSlotData;
import io.github.some_example_name.repository.database.LocalKeyValueDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SaveRepository {
    private static final String LEGACY_SAVE_FILE_PATH = "saves/save-slots.json";
    private static final String DATABASE_KEY = "save-slots";
    private static final int SLOT_COUNT = 4;

    private final Json json = new Json();
    private final LocalKeyValueDatabase database = LocalKeyValueDatabase.getInstance();

    private int lastLoadedSlotNumber = 1;

    public List<SaveSlot> loadSlots() {
        String content = database.get(DATABASE_KEY);
        if (content == null || content.isBlank()) {
            content = readLegacyFile();
        }

        SaveFileData saveFileData = parseSaveFileData(content);
        List<SaveSlot> slots = saveFileData == null
            ? createEmptySlots()
            : createSlotsFromData(saveFileData);

        lastLoadedSlotNumber = normalizeSlotNumber(
            saveFileData == null
                ? 1
                : saveFileData.lastPlayedSlotNumber
        );

        if (!database.contains(DATABASE_KEY)) {
            saveSlots(slots, lastLoadedSlotNumber);
        }

        return slots;
    }

    public int getLastLoadedSlotNumber() {
        return lastLoadedSlotNumber;
    }

    public void saveSlots(List<SaveSlot> slots) {
        saveSlots(slots, lastLoadedSlotNumber);
    }

    public void saveSlots(
        List<SaveSlot> slots,
        int lastPlayedSlotNumber
    ) {
        List<SaveSlot> safeSlots = normalizeSlots(slots);
        SaveFileData saveFileData = new SaveFileData();

        lastLoadedSlotNumber = normalizeSlotNumber(lastPlayedSlotNumber);
        saveFileData.lastPlayedSlotNumber = lastLoadedSlotNumber;

        for (SaveSlot slot : safeSlots) {
            saveFileData.slots.add(slot.toData());
        }

        database.put(DATABASE_KEY, json.prettyPrint(saveFileData));
    }

    private SaveFileData parseSaveFileData(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        try {
            return json.fromJson(SaveFileData.class, content);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String readLegacyFile() {
        FileHandle saveFile = Gdx.files.local(LEGACY_SAVE_FILE_PATH);
        if (!saveFile.exists()) {
            return "";
        }
        try {
            return saveFile.readString("UTF-8");
        } catch (RuntimeException exception) {
            return "";
        }
    }

    private List<SaveSlot> createSlotsFromData(SaveFileData saveFileData) {
        if (saveFileData == null || saveFileData.slots == null) {
            return createEmptySlots();
        }

        List<SaveSlot> slots = createEmptySlots();
        Set<Integer> loadedSlotNumbers = new HashSet<>();

        for (SaveSlotData slotData : saveFileData.slots) {
            if (!isValidSlotData(slotData) || loadedSlotNumbers.contains(slotData.slotNumber)) {
                continue;
            }

            SaveSlot slot = slots.get(slotData.slotNumber - 1);
            slot.loadFromData(slotData);
            loadedSlotNumbers.add(slotData.slotNumber);
        }

        return slots;
    }

    private boolean isValidSlotData(SaveSlotData slotData) {
        return slotData != null
            && slotData.slotNumber >= 1
            && slotData.slotNumber <= SLOT_COUNT;
    }

    private List<SaveSlot> normalizeSlots(List<SaveSlot> slots) {
        List<SaveSlot> normalizedSlots = createEmptySlots();
        if (slots == null) {
            return normalizedSlots;
        }

        Set<Integer> savedSlotNumbers = new HashSet<>();
        for (SaveSlot slot : slots) {
            if (slot == null) {
                continue;
            }

            int slotNumber = slot.getSlotNumber();
            if (slotNumber < 1 || slotNumber > SLOT_COUNT || savedSlotNumbers.contains(slotNumber)) {
                continue;
            }

            normalizedSlots.set(slotNumber - 1, slot);
            savedSlotNumbers.add(slotNumber);
        }

        return normalizedSlots;
    }

    private int normalizeSlotNumber(int slotNumber) {
        if (slotNumber < 1 || slotNumber > SLOT_COUNT) {
            return 1;
        }

        return slotNumber;
    }

    private List<SaveSlot> createEmptySlots() {
        List<SaveSlot> slots = new ArrayList<>();
        for (int slotNumber = 1; slotNumber <= SLOT_COUNT; slotNumber++) {
            slots.add(new SaveSlot(slotNumber));
        }
        return slots;
    }
}
