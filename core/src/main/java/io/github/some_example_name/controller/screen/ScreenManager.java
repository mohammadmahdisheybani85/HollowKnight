package io.github.some_example_name.controller.screen;

import com.badlogic.gdx.Screen;
import io.github.some_example_name.Main;
import io.github.some_example_name.view.screen.*;

public class ScreenManager {
    private final Main game;

    public ScreenManager(Main game) {
        this.game = game;
    }

    public void show(ScreenType screenType) {
        updateMusicForScreen(screenType);

        Screen currentScreen = game.getScreen();

        if (currentScreen != null) {
            currentScreen.dispose();
        }

        Screen nextScreen = createScreen(screenType);
        game.setScreen(nextScreen);
    }

    private void updateMusicForScreen(ScreenType screenType) {
        if (screenType == ScreenType.GAME) {
            game.getAudioManager().stopMusic();
            return;
        }

        if (screenType == ScreenType.VICTORY) {
            game.getAudioManager().playVictoryMusic();
            return;
        }

        game.getAudioManager().playMenuMusic();
    }

    private Screen createScreen(ScreenType screenType) {
        return switch (screenType) {
            case MAIN_MENU -> new MainMenuScreen(game);
            case GAME_START -> new GameStartScreen(game);
            case SETTINGS -> new SettingsScreen(game);
            case GUIDE -> new GuideScreen(game);
            case ACHIEVEMENTS -> new AchievementsScreen(game);
            case GAME -> new GameScreen(game);
            case VICTORY -> new VictoryScreen(game);
        };
    }
}
