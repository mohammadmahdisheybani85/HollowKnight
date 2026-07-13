package io.github.some_example_name.repository.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A tiny embedded key-value document database used by the desktop game.
 * Data is stored in one binary file rather than separate JSON save files.
 */
public final class LocalKeyValueDatabase {
    private static final String LOG_TAG = "LocalKeyValueDatabase";
    private static final String DATABASE_PATH = "saves/game-data.kvdb";
    private static final String MAGIC = "APHW4DB";
    private static final int VERSION = 1;
    private static final int MAX_ENTRY_BYTES = 16 * 1024 * 1024;

    private static LocalKeyValueDatabase instance;

    private final FileHandle databaseFile;
    private final Map<String, String> documents = new LinkedHashMap<>();

    private boolean loaded;

    private LocalKeyValueDatabase() {
        databaseFile = Gdx.files.local(DATABASE_PATH);
    }

    public static synchronized LocalKeyValueDatabase getInstance() {
        if (instance == null) {
            instance = new LocalKeyValueDatabase();
        }
        return instance;
    }

    public synchronized String get(String key) {
        ensureLoaded();
        return documents.get(key);
    }

    public synchronized boolean contains(String key) {
        ensureLoaded();
        return documents.containsKey(key);
    }

    public synchronized void put(String key, String value) {
        if (key == null || key.isBlank() || value == null) {
            return;
        }

        ensureLoaded();
        documents.put(key, value);
        flush();
    }

    public synchronized void remove(String key) {
        ensureLoaded();
        if (documents.remove(key) != null) {
            flush();
        }
    }

    private void ensureLoaded() {
        if (loaded) {
            return;
        }

        loaded = true;
        documents.clear();

        if (!databaseFile.exists()) {
            return;
        }

        try (DataInputStream input = new DataInputStream(
            new BufferedInputStream(databaseFile.read())
        )) {
            String magic = input.readUTF();
            int version = input.readInt();

            if (!MAGIC.equals(magic) || version != VERSION) {
                throw new IOException("Unsupported database format.");
            }

            int count = input.readInt();
            if (count < 0 || count > 10_000) {
                throw new IOException("Invalid database entry count.");
            }

            for (int index = 0; index < count; index++) {
                String key = input.readUTF();
                int byteCount = input.readInt();

                if (byteCount < 0 || byteCount > MAX_ENTRY_BYTES) {
                    throw new IOException("Invalid database entry size.");
                }

                byte[] bytes = input.readNBytes(byteCount);
                if (bytes.length != byteCount) {
                    throw new EOFException("Unexpected end of database file.");
                }

                documents.put(key, new String(bytes, StandardCharsets.UTF_8));
            }
        } catch (IOException | RuntimeException exception) {
            documents.clear();
            Gdx.app.error(LOG_TAG, "Could not read local database; starting with an empty database.", exception);
        }
    }

    private void flush() {
        databaseFile.parent().mkdirs();
        FileHandle temporaryFile = databaseFile.sibling(databaseFile.name() + ".tmp");

        try (DataOutputStream output = new DataOutputStream(
            new BufferedOutputStream(temporaryFile.write(false))
        )) {
            output.writeUTF(MAGIC);
            output.writeInt(VERSION);
            output.writeInt(documents.size());

            for (Map.Entry<String, String> entry : documents.entrySet()) {
                byte[] bytes = entry.getValue().getBytes(StandardCharsets.UTF_8);
                output.writeUTF(entry.getKey());
                output.writeInt(bytes.length);
                output.write(bytes);
            }
        } catch (IOException | RuntimeException exception) {
            Gdx.app.error(LOG_TAG, "Could not write local database.", exception);
            return;
        }

        if (databaseFile.exists() && !databaseFile.delete()) {
            Gdx.app.error(LOG_TAG, "Could not replace the old database file.");
            temporaryFile.delete();
            return;
        }

        temporaryFile.moveTo(databaseFile);
    }
}
