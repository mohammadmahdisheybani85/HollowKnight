package io.github.some_example_name.controller.cheat;

import io.github.some_example_name.model.player.PlayerStats;

public class CheatCodeController {
    private static final int MAX_BUFFER_LENGTH = 20;

    private final PlayerStats playerStats;
    private String typedBuffer = "";

    public CheatCodeController(PlayerStats playerStats) {
        this.playerStats = playerStats;
    }

    public String handleTypedCharacter(char character) {
        if (!Character.isLetterOrDigit(character)) {
            return "";
        }

        typedBuffer += Character.toUpperCase(character);

        if (typedBuffer.length() > MAX_BUFFER_LENGTH) {
            typedBuffer = typedBuffer.substring(typedBuffer.length() - MAX_BUFFER_LENGTH);
        }

        if (typedBuffer.endsWith("GODMODE")) {
            playerStats.toggleGodMode();
            clearBuffer();

            if (playerStats.isGodModeEnabled()) {
                return "GODMODE activated.";
            }

            return "GODMODE deactivated.";
        }

        if (typedBuffer.endsWith("FULLSOUL")) {
            playerStats.fillSoul();
            clearBuffer();
            return "Soul bar filled.";
        }

        return "";
    }

    private void clearBuffer() {
        typedBuffer = "";
    }
}
