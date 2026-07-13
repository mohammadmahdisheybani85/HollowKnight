package io.github.some_example_name.controller.game;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.some_example_name.util.L10n;

public class GameMessageController {
    private static final float DEFAULT_MESSAGE_DURATION = 2.2f;

    private final Label messageLabel;
    private final String defaultMessage;

    private float messageTimer;

    public GameMessageController(Label messageLabel, String defaultMessage) {
        this.messageLabel = messageLabel;
        this.defaultMessage = defaultMessage;
        this.messageTimer = 0f;
        showDefaultMessage();
    }

    public void update(float delta) {
        if (messageTimer <= 0f) return;
        messageTimer -= delta;
        if (messageTimer <= 0f) showDefaultMessage();
    }

    public void showTemporaryMessage(String message) {
        showTemporaryMessage(message, DEFAULT_MESSAGE_DURATION);
    }

    public void showTemporaryMessage(String message, float duration) {
        if (message == null || message.isBlank()) return;
        messageLabel.setText(L10n.dynamic(message));
        messageTimer = duration;
    }

    public void showDefaultMessage() {
        messageLabel.setText(L10n.dynamic(defaultMessage));
        messageTimer = 0f;
    }
}
