package io.github.some_example_name.repository.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.some_example_name.model.settings.GameSettingsData;
import io.github.some_example_name.repository.database.LocalKeyValueDatabase;

public class GameSettingsRepository {
    private static final String LEGACY_SETTINGS_FILE_PATH = "saves/settings.json";
    private static final String DATABASE_KEY = "settings";

    private final Json json = new Json();
    private final LocalKeyValueDatabase database = LocalKeyValueDatabase.getInstance();

    public GameSettingsData load() {
        String content = database.get(DATABASE_KEY);

        if (content == null || content.isBlank()) {
            content = readLegacyFile();
        }

        GameSettingsData data = parse(content);

        if (!database.contains(DATABASE_KEY)) {
            database.put(DATABASE_KEY, json.prettyPrint(data));
        }

        return data;
    }

    public void save(GameSettingsData data) {
        if (data == null) {
            return;
        }

        database.put(DATABASE_KEY, json.prettyPrint(data));
    }

    private GameSettingsData parse(String content) {
        if (content == null || content.isBlank()) {
            return new GameSettingsData();
        }

        try {
            GameSettingsData data = json.fromJson(GameSettingsData.class, content);
            return data == null ? new GameSettingsData() : data;
        } catch (RuntimeException exception) {
            return new GameSettingsData();
        }
    }

    private String readLegacyFile() {
        FileHandle fileHandle = Gdx.files.local(LEGACY_SETTINGS_FILE_PATH);
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
