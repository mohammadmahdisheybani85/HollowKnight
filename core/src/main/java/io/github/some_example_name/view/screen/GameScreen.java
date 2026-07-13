package io.github.some_example_name.view.screen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.controller.game.GameAchievementPopupController;
import io.github.some_example_name.controller.game.GameAreaTitleController;
import io.github.some_example_name.controller.game.GameAreaTransitionController;
import io.github.some_example_name.controller.game.GameAreaTransitionFeedbackController;
import io.github.some_example_name.controller.game.GameBossHudController;
import io.github.some_example_name.controller.game.GameCharmEffectController;
import io.github.some_example_name.controller.game.GameCheatInputController;
import io.github.some_example_name.controller.game.GameCombatSpellController;
import io.github.some_example_name.controller.game.GameDamageProgressController;
import io.github.some_example_name.controller.game.GameEnemyUpdateController;
import io.github.some_example_name.controller.game.GameHudController;
import io.github.some_example_name.controller.game.GameLoadedProgressController;
import io.github.some_example_name.controller.game.GameMenuOverlayController;
import io.github.some_example_name.controller.game.GameMessageController;
import io.github.some_example_name.controller.game.GamePlayerActionController;
import io.github.some_example_name.controller.game.GameProgressFeedbackController;
import io.github.some_example_name.controller.game.GameRenderController;
import io.github.some_example_name.controller.game.GameRunTimerController;
import io.github.some_example_name.controller.game.GameSaveController;
import io.github.some_example_name.controller.game.GameSessionController;
import io.github.some_example_name.controller.game.GameVictoryController;
import io.github.some_example_name.controller.game.GameWorldLoadController;
import io.github.some_example_name.controller.game.GameZoteInteractionController;
import io.github.some_example_name.controller.game.GuardianLaserDamageController;
import io.github.some_example_name.controller.game.PlayerDamageController;
import io.github.some_example_name.model.game.GameSessionState;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.input.GameInputState;
import io.github.some_example_name.view.input.GameInputReader;
import io.github.some_example_name.view.ui.GameControlsHintBuilder;

public class GameScreen extends AbstractMenuScreen {
    private static final float MESSAGE_HORIZONTAL_MARGIN = 55f;
    private static final float MESSAGE_BOTTOM_MARGIN = 12f;
    private static final float MESSAGE_HEIGHT = 58f;

    private GameWorld world;
    private GameSessionState sessionState;
    private GameInputReader inputReader;

    private GameSessionController sessionController;
    private GameEnemyUpdateController enemyUpdateController;
    private GameAreaTitleController areaTitleController;
    private GameMenuOverlayController menuOverlayController;
    private GameCheatInputController cheatInputController;
    private GameDamageProgressController damageProgressController;
    private GameCombatSpellController combatSpellController;
    private GamePlayerActionController playerActionController;
    private GameAreaTransitionFeedbackController areaTransitionFeedbackController;
    private GameRunTimerController runTimerController;
    private GameLoadedProgressController loadedProgressController;
    private GameWorldLoadController worldLoadController;
    private GameZoteInteractionController zoteInteractionController;

    private GameMessageController gameMessageController;
    private GameAreaTransitionController areaTransitionController;
    private GameVictoryController victoryController;
    private GameSaveController saveController;
    private GameProgressFeedbackController progressFeedbackController;
    private GameBossHudController bossHudController;
    private GameAchievementPopupController achievementPopupController;
    private GameHudController hudController;
    private GameCharmEffectController charmEffectController;
    private GameRenderController renderController;

    private Label messageLabel;

    public GameScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        setupWorld();
        setupGameState();
        setupGameplayControllers();
        setupRendering();
        setupMessageSystem();
        setupPlayerActionController();
        setupCombatSpellController();
        setupDamageProgressController();
        setupUiControllers();
        setupMenuOverlays();
    }

    @Override
    public void render(float delta) {
        game.getAudioManager().update(delta);
        renderController.clear(world);

        updateGame(delta);

        if (victoryController.isVictoryTriggered()) {
            return;
        }

        renderController.renderWorld(
            world,
            game.getPlayerStats(),
            delta
        );

        refreshBrightnessOverlay();

        stage.act(delta);
        stage.draw();
    }

    private void updateGame(float delta) {
        gameMessageController.update(delta);

        GameInputState inputState = inputReader.read();

        if (updateSessionAndMenus(inputState)) {
            return;
        }

        runTimerController.update(delta);

        if (zoteInteractionController.update(
            delta,
            inputState
        )) {
            updateGameplayUi();
            return;
        }

        boolean playerIsFocusing =
            updateFocusAndPlayerMovement(
                delta,
                inputState
            );

        updateEnemies(delta);
        updateCombatAndSpells(
            delta,
            inputState,
            playerIsFocusing
        );
        updateDamageAndProgress(delta);

        if (
            victoryController.checkVictory(
                runTimerController.getElapsedTimeSeconds()
            )
        ) {
            return;
        }

        handleAreaTransition();
        updateGameplayUi();
    }

    private boolean updateSessionAndMenus(
        GameInputState inputState
    ) {
        charmEffectController.update();
        sessionController.update(inputState);
        menuOverlayController.update(inputState);

        if (!menuOverlayController.isGameplayBlocked()) {
            return false;
        }

        updateGameplayUi();
        return true;
    }

    private boolean updateFocusAndPlayerMovement(
        float delta,
        GameInputState inputState
    ) {
        return playerActionController.update(
            delta,
            inputState
        );
    }

    private void updateEnemies(float delta) {
        enemyUpdateController.update(delta);
    }

    private void updateCombatAndSpells(
        float delta,
        GameInputState inputState,
        boolean playerIsFocusing
    ) {
        combatSpellController.update(
            delta,
            inputState,
            playerIsFocusing
        );
    }

    private void updateDamageAndProgress(float delta) {
        damageProgressController.update(delta);
    }

    private void updateGameplayUi() {
        hudController.update(game.getPlayerStats());
        bossHudController.update();
    }

    private void saveAndQuit() {
        saveController.saveAndQuit(
            runTimerController.getElapsedTimeSeconds()
        );
    }

    private void handleAreaTransition() {
        areaTransitionFeedbackController.update();
    }

    @Override
    protected InputProcessor createInputProcessor() {
        return new InputMultiplexer(
            stage,
            new InputAdapter() {
                @Override
                public boolean keyTyped(char character) {
                    return cheatInputController != null
                        && cheatInputController
                        .handleTypedCharacter(character);
                }
            }
        );
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (hudController != null) {
            hudController.position(stage);
        }

        if (bossHudController != null) {
            bossHudController.position(stage);
        }

        if (achievementPopupController != null) {
            achievementPopupController.position(stage);
        }

        if (zoteInteractionController != null) {
            zoteInteractionController.position(stage);
        }

        positionMessageLabel();
    }

    @Override
    public void dispose() {
        if (achievementPopupController != null) {
            achievementPopupController.dispose();
        }

        if (hudController != null) {
            hudController.dispose();
        }

        if (zoteInteractionController != null) {
            zoteInteractionController.dispose();
        }

        if (renderController != null) {
            renderController.dispose();
        }

        super.dispose();
    }

    private void setupWorld() {
        worldLoadController = new GameWorldLoadController(game);
        world = worldLoadController.createLoadedWorld();

        areaTransitionController =
            new GameAreaTransitionController(
                world,
                game.getAudioManager()
            );

        victoryController =
            new GameVictoryController(game, world);

        saveController =
            new GameSaveController(game, world);

        loadedProgressController =
            new GameLoadedProgressController(game);

        loadedProgressController
            .syncEnemyKillTrackerWithLoadedSave();

        runTimerController = new GameRunTimerController(
            worldLoadController
                .getLoadedElapsedTimeSeconds()
        );
    }

    private void setupGameState() {
        sessionState = new GameSessionState();
        inputReader = new GameInputReader(
            game.getKeyBindingController()
        );
        sessionController =
            new GameSessionController(sessionState);
    }

    private void setupGameplayControllers() {
        enemyUpdateController =
            new GameEnemyUpdateController(world);

        charmEffectController =
            new GameCharmEffectController(game);
    }

    private void setupRendering() {
        renderController = new GameRenderController(game.getInventoryController());
    }

    private void setupMessageSystem() {
        String controlsHintText =
            new GameControlsHintBuilder(
                game.getKeyBindingController()
            ).build();

        messageLabel = new Label(
            controlsHintText,
            skin
        );

        messageLabel.setFontScale(0.62f);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);
        messageLabel.setColor(
            new Color(
                0.88f,
                0.92f,
                0.97f,
                0.92f
            )
        );

        gameMessageController =
            new GameMessageController(
                messageLabel,
                controlsHintText
            );

        progressFeedbackController =
            new GameProgressFeedbackController(
                game.getPlayerStats(),
                game.getAchievementController(),
                gameMessageController
            );

        positionMessageLabel();
        stage.addActor(messageLabel);
    }

    private void positionMessageLabel() {
        if (messageLabel == null || stage == null) {
            return;
        }

        float stageWidth =
            stage.getViewport().getWorldWidth();

        float labelWidth = Math.max(
            100f,
            stageWidth - MESSAGE_HORIZONTAL_MARGIN * 2f
        );

        messageLabel.setSize(
            labelWidth,
            MESSAGE_HEIGHT
        );

        messageLabel.setPosition(
            MESSAGE_HORIZONTAL_MARGIN,
            MESSAGE_BOTTOM_MARGIN
        );
    }

    private void setupUiControllers() {
        hudController =
            new GameHudController(skin, stage);

        hudController.update(
            game.getPlayerStats()
        );

        bossHudController =
            new GameBossHudController(
                world,
                skin,
                stage
            );

        achievementPopupController =
            new GameAchievementPopupController(
                game.getAchievementController(),
                skin,
                stage
            );

        zoteInteractionController =
            new GameZoteInteractionController(
                world,
                game,
                skin,
                stage,
                gameMessageController
            );
    }

    private void setupMenuOverlays() {
        menuOverlayController =
            new GameMenuOverlayController(
                game,
                sessionState,
                sessionController,
                skin,
                stage,
                this::saveAndQuit
            );

        cheatInputController =
            new GameCheatInputController(
                game,
                world,
                menuOverlayController,
                gameMessageController,
                progressFeedbackController,
                hudController
            );

        areaTitleController =
            new GameAreaTitleController(skin, stage);

        areaTitleController.showArea(
            world.getCurrentArea().getDisplayName()
        );

        areaTransitionFeedbackController =
            new GameAreaTransitionFeedbackController(
                areaTransitionController,
                gameMessageController,
                areaTitleController
            );
    }

    private void setupDamageProgressController() {
        PlayerDamageController playerDamageController =
            new PlayerDamageController(
                world,
                game.getPlayerStats(),
                game.getInventoryController(),
                game.getAudioManager()
            );

        GuardianLaserDamageController guardianLaserDamageController =
            new GuardianLaserDamageController(
                world,
                game.getPlayerStats(),
                game.getAudioManager()
            );

        damageProgressController =
            new GameDamageProgressController(
                playerDamageController,
                guardianLaserDamageController,
                progressFeedbackController,
                gameMessageController
            );
    }

    private void setupCombatSpellController() {
        combatSpellController =
            new GameCombatSpellController(
                world,
                game,
                gameMessageController
            );
    }

    private void setupPlayerActionController() {
        playerActionController =
            new GamePlayerActionController(
                world,
                game,
                gameMessageController
            );
    }
}
