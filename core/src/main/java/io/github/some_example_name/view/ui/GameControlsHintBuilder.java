package io.github.some_example_name.view.ui;

import io.github.some_example_name.controller.input.KeyBindingController;
import io.github.some_example_name.model.input.PlayerAction;
import io.github.some_example_name.util.L10n;

public class GameControlsHintBuilder {
    private final KeyBindingController keyBindingController;

    public GameControlsHintBuilder(KeyBindingController keyBindingController) {
        this.keyBindingController = keyBindingController;
    }

    public String build() {
        String move = key(PlayerAction.MOVE_LEFT) + "/" + key(PlayerAction.MOVE_RIGHT);
        if (L10n.isFrench()) {
            return move + " deplacement | "
                + key(PlayerAction.JUMP) + " saut | "
                + key(PlayerAction.DASH) + " ruee | "
                + key(PlayerAction.ATTACK) + " attaque | "
                + key(PlayerAction.CAST_SPELL) + " sort | "
                + key(PlayerAction.LOOK_UP) + "+" + key(PlayerAction.CAST_SPELL) + " sort vertical | "
                + key(PlayerAction.LOOK_DOWN) + "+" + key(PlayerAction.ATTACK) + " pogo | "
                + key(PlayerAction.FOCUS) + " soin | "
                + key(PlayerAction.INTERACT) + " interagir | "
                + key(PlayerAction.OPEN_INVENTORY) + " inventaire | "
                + key(PlayerAction.PAUSE) + " pause";
        }
        return move + " move | "
            + key(PlayerAction.JUMP) + " jump | "
            + key(PlayerAction.DASH) + " dash | "
            + key(PlayerAction.ATTACK) + " attack | "
            + key(PlayerAction.CAST_SPELL) + " spell | "
            + key(PlayerAction.LOOK_UP) + "+" + key(PlayerAction.CAST_SPELL) + " up spell | "
            + key(PlayerAction.LOOK_DOWN) + "+" + key(PlayerAction.ATTACK) + " pogo | "
            + key(PlayerAction.FOCUS) + " focus | "
            + key(PlayerAction.INTERACT) + " interact | "
            + key(PlayerAction.OPEN_INVENTORY) + " inventory | "
            + key(PlayerAction.PAUSE) + " pause";
    }

    private String key(PlayerAction action) {
        return keyBindingController.getKeyName(action);
    }
}
