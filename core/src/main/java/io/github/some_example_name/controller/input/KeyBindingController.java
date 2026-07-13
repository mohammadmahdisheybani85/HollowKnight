package io.github.some_example_name.controller.input;

import com.badlogic.gdx.Input;
import io.github.some_example_name.model.input.KeyBindingData;
import io.github.some_example_name.model.input.KeyBindingFileData;
import io.github.some_example_name.model.input.KeyBindingSettings;
import io.github.some_example_name.model.input.PlayerAction;
import io.github.some_example_name.repository.input.KeyBindingRepository;

import java.util.Map;

public class KeyBindingController {
    private final KeyBindingSettings settings;
    private final KeyBindingRepository repository;

    public KeyBindingController(KeyBindingSettings settings) {
        this.settings = settings;
        this.repository = new KeyBindingRepository();

        loadSavedKeyBindings();
    }

    public int getKeyCode(PlayerAction action) {
        return settings.getKeyCode(action);
    }

    public String getKeyName(PlayerAction action) {
        return Input.Keys.toString(settings.getKeyCode(action));
    }

    public boolean changeKey(PlayerAction action, int keyCode) {
        if (!isValidKeyCode(keyCode)) {
            return false;
        }

        if (isKeyUsedByAnotherAction(action, keyCode)) {
            return false;
        }

        settings.setKeyCode(action, keyCode);
        saveKeyBindings();

        return true;
    }

    public PlayerAction findActionUsingKey(int keyCode) {
        for (Map.Entry<PlayerAction, Integer> entry : settings.getKeyCodesCopy().entrySet()) {
            if (entry.getValue() == keyCode) {
                return entry.getKey();
            }
        }

        return null;
    }

    public boolean isKeyUsedByAnotherAction(PlayerAction currentAction, int keyCode) {
        PlayerAction actionUsingKey = findActionUsingKey(keyCode);

        return actionUsingKey != null && actionUsingKey != currentAction;
    }

    public void resetToDefaults() {
        settings.resetToDefaults();
        saveKeyBindings();
    }

    private void loadSavedKeyBindings() {
        KeyBindingFileData data = repository.load();

        if (data == null) {
            saveKeyBindings();
            return;
        }

        if (data.version != KeyBindingSettings.DEFAULT_BINDING_VERSION) {
            settings.resetToDefaults();
            saveKeyBindings();
            return;
        }

        if (data.keyBindings == null || data.keyBindings.isEmpty()) {
            saveKeyBindings();
            return;
        }

        for (KeyBindingData keyBindingData : data.keyBindings) {
            loadSingleKeyBinding(keyBindingData);
        }
    }

    private void loadSingleKeyBinding(KeyBindingData keyBindingData) {
        if (keyBindingData == null) {
            return;
        }

        if (keyBindingData.actionName == null || keyBindingData.actionName.isBlank()) {
            return;
        }

        if (!isValidKeyCode(keyBindingData.keyCode)) {
            return;
        }

        try {
            PlayerAction action = PlayerAction.valueOf(keyBindingData.actionName);

            if (isKeyUsedByAnotherAction(action, keyBindingData.keyCode)) {
                return;
            }

            settings.setKeyCode(action, keyBindingData.keyCode);
        } catch (IllegalArgumentException ignored) {

        }
    }

    private boolean isValidKeyCode(int keyCode) {
        return keyCode > 0;
    }

    private void saveKeyBindings() {
        KeyBindingFileData data = new KeyBindingFileData();
        data.version = KeyBindingSettings.DEFAULT_BINDING_VERSION;

        for (Map.Entry<PlayerAction, Integer> entry : settings.getKeyCodesCopy().entrySet()) {
            data.keyBindings.add(new KeyBindingData(
                entry.getKey().name(),
                entry.getValue()
            ));
        }

        repository.save(data);
    }
}
