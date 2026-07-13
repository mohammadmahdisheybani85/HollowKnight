package io.github.some_example_name.repository.achievement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.some_example_name.model.achievement.GlobalAchievementData;
import io.github.some_example_name.repository.database.LocalKeyValueDatabase;

import java.util.ArrayList;

public class GlobalAchievementRepository {
    private static final String LEGACY_FILE_PATH = "saves/global-achievements.json";
    private static final String DATABASE_KEY = "global-achievements";

    private final Json json = new Json();
    private final LocalKeyValueDatabase database = LocalKeyValueDatabase.getInstance();

    public GlobalAchievementData load() {
        String content = database.get(DATABASE_KEY);
        if (content == null || content.isBlank()) {
            content = readLegacyFile();
        }

        GlobalAchievementData data = parse(content);
        normalize(data);

        if (!database.contains(DATABASE_KEY)) {
            database.put(DATABASE_KEY, json.prettyPrint(data));
        }

        return data;
    }

    public void save(GlobalAchievementData data) {
        if (data == null) {
            return;
        }
        normalize(data);
        database.put(DATABASE_KEY, json.prettyPrint(data));
    }

    private GlobalAchievementData parse(String content) {
        if (content == null || content.isBlank()) {
            return new GlobalAchievementData();
        }

        try {
            GlobalAchievementData data = json.fromJson(GlobalAchievementData.class, content);
            return data == null ? new GlobalAchievementData() : data;
        } catch (RuntimeException exception) {
            return new GlobalAchievementData();
        }
    }

    private void normalize(GlobalAchievementData data) {
        if (data.unlockedAchievementNames == null) {
            data.unlockedAchievementNames = new ArrayList<>();
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
