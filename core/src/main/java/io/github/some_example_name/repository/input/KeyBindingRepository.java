package io.github.some_example_name.repository.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.some_example_name.model.input.KeyBindingFileData;
import io.github.some_example_name.repository.database.LocalKeyValueDatabase;

public class KeyBindingRepository {
    private static final String LEGACY_FILE_PATH = "saves/key-bindings.json";
    private static final String DATABASE_KEY = "key-bindings";

    private final Json json = new Json();
    private final LocalKeyValueDatabase database = LocalKeyValueDatabase.getInstance();

    public KeyBindingFileData load() {
        String content = database.get(DATABASE_KEY);
        if (content == null || content.isBlank()) {
            content = readLegacyFile();
        }

        KeyBindingFileData data = parse(content);
        if (!database.contains(DATABASE_KEY)) {
            database.put(DATABASE_KEY, json.prettyPrint(data));
        }
        return data;
    }

    public void save(KeyBindingFileData data) {
        if (data == null) {
            return;
        }
        database.put(DATABASE_KEY, json.prettyPrint(data));
    }

    private KeyBindingFileData parse(String content) {
        if (content == null || content.isBlank()) {
            return new KeyBindingFileData();
        }

        try {
            KeyBindingFileData data = json.fromJson(KeyBindingFileData.class, content);
            if (data == null || data.keyBindings == null) {
                return new KeyBindingFileData();
            }
            return data;
        } catch (RuntimeException exception) {
            return new KeyBindingFileData();
        }
    }

    private String readLegacyFile() {
        FileHandle fileHandle = Gdx.files.local(LEGACY_FILE_PATH);
        if (!fileHandle.exists()) {
            return "";
        }
        try {
            return fileHandle.readString("UTF-8");
        } catch (RuntimeException exception) {
            return "";
        }
    }
}
