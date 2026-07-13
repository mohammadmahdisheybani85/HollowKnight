package io.github.some_example_name.view.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.some_example_name.Main;
import io.github.some_example_name.util.L10n;

public class VictoryScreen extends AbstractMenuScreen {
    public VictoryScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        Table table = createCenteredTable();

        Label titleLabel = uiFactory.createTitle(L10n.tr("VICTORY"));
        titleLabel.setFontScale(2f);
        table.add(titleLabel).padBottom(26f).row();

        table.add(new Label(L10n.tr("False Knight has been defeated."), skin))
            .padBottom(8f).row();
        table.add(new Label(L10n.tr("Hallownest is one step safer."), skin))
            .padBottom(22f).row();

        addStats(table);

        uiFactory.addMenuButton(table, uiFactory.createButton(
            L10n.tr("Restart"),
            this::restartGame
        ));
        uiFactory.addMenuButton(table, uiFactory.createButton(
            L10n.tr("Achievements"),
            () -> game.getScreenManager().show(ScreenType.ACHIEVEMENTS)
        ));
        uiFactory.addMenuButton(table, uiFactory.createButton(
            L10n.tr("Main Menu"),
            () -> game.getScreenManager().show(ScreenType.MAIN_MENU)
        ));
    }

    private void restartGame() {
        game.getGameStartController().restartCurrentGame(
            game.getPlayerStats(),
            game.getInventoryController()
        );
        game.getEnemyKillTracker().reset();
        game.getAchievementController().loadFromSave(
            game.getGameStartController()
                .getCurrentSlotAchievementNames()
        );
        game.getScreenManager().show(ScreenType.GAME);
    }

    private void addStats(Table table) {
        table.add(new Label(L10n.tr("Run Statistics"), skin))
            .padBottom(8f).row();
        table.add(new Label(L10n.dynamic(
            "Elapsed Time: " + formatTime(game.getLastRunElapsedTimeSeconds())
        ), skin)).padBottom(6f).row();
        table.add(new Label(L10n.dynamic(
            "Enemies Killed: " + game.getLastRunEnemyKills()
        ), skin)).padBottom(6f).row();
        table.add(new Label(L10n.dynamic(
            "Deaths: " + game.getLastRunDeathCount()
        ), skin)).padBottom(6f).row();
        table.add(new Label(L10n.tr("Status: Completed"), skin))
            .padBottom(24f).row();
    }

    private String formatTime(float elapsedSeconds) {
        int totalSeconds = Math.max(0, (int) elapsedSeconds);
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    protected String getBackgroundPath() {
        return "backgrounds/menu/victory.jpeg";
    }
}
