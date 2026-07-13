package io.github.some_example_name.model.input;

import com.badlogic.gdx.Input;

import java.util.EnumMap;
import java.util.Map;

public class KeyBindingSettings {
    public static final int DEFAULT_BINDING_VERSION = 4;

    private final Map<PlayerAction, Integer> keyCodes = new EnumMap<>(PlayerAction.class);

    public KeyBindingSettings() {
        resetToDefaults();
    }

    public int getKeyCode(PlayerAction action) {
        Integer keyCode = keyCodes.get(action);
        if (keyCode == null) {
            resetMissingBinding(action);
            keyCode = keyCodes.get(action);
        }
        return keyCode;
    }

    public void setKeyCode(PlayerAction action, int keyCode) {
        keyCodes.put(action, keyCode);
    }

    public Map<PlayerAction, Integer> getKeyCodesCopy() {
        return new EnumMap<>(keyCodes);
    }

    public void resetToDefaults() {
        keyCodes.clear();

        keyCodes.put(PlayerAction.MOVE_LEFT, Input.Keys.A);
        keyCodes.put(PlayerAction.MOVE_RIGHT, Input.Keys.D);
        keyCodes.put(PlayerAction.LOOK_UP, Input.Keys.W);
        keyCodes.put(PlayerAction.LOOK_DOWN, Input.Keys.S);
        keyCodes.put(PlayerAction.JUMP, Input.Keys.SPACE);
        keyCodes.put(PlayerAction.DASH, Input.Keys.SHIFT_LEFT);
        keyCodes.put(PlayerAction.ATTACK, Input.Keys.J);
        keyCodes.put(PlayerAction.CAST_SPELL, Input.Keys.K);
        keyCodes.put(PlayerAction.FOCUS, Input.Keys.F);
        keyCodes.put(PlayerAction.INTERACT, Input.Keys.E);
        keyCodes.put(PlayerAction.OPEN_INVENTORY, Input.Keys.I);
        keyCodes.put(PlayerAction.PAUSE, Input.Keys.ESCAPE);
    }

    private void resetMissingBinding(PlayerAction action) {
        switch (action) {
            case MOVE_LEFT -> keyCodes.put(action, Input.Keys.A);
            case MOVE_RIGHT -> keyCodes.put(action, Input.Keys.D);
            case LOOK_UP -> keyCodes.put(action, Input.Keys.W);
            case LOOK_DOWN -> keyCodes.put(action, Input.Keys.S);
            case JUMP -> keyCodes.put(action, Input.Keys.SPACE);
            case DASH -> keyCodes.put(action, Input.Keys.SHIFT_LEFT);
            case ATTACK -> keyCodes.put(action, Input.Keys.J);
            case CAST_SPELL -> keyCodes.put(action, Input.Keys.K);
            case FOCUS -> keyCodes.put(action, Input.Keys.F);
            case INTERACT -> keyCodes.put(action, Input.Keys.E);
            case OPEN_INVENTORY -> keyCodes.put(action, Input.Keys.I);
            case PAUSE -> keyCodes.put(action, Input.Keys.ESCAPE);
        }
    }
}
