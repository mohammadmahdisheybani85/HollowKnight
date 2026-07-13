package io.github.some_example_name.view.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.controller.start.GameStartController;
import io.github.some_example_name.model.save.SaveSlot;
import io.github.some_example_name.util.L10n;

public class GameStartScreen extends AbstractMenuScreen {
    private static final int NO_PENDING_DELETE_SLOT = -1;

    private GameStartController controller;
    private Label messageLabel;

    private Table confirmationBox;
    private Label confirmationLabel;

    private SlotSelectionMode selectionMode = SlotSelectionMode.NONE;
    private int pendingDeleteSlotNumber = NO_PENDING_DELETE_SLOT;

    public GameStartScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        controller = game.getGameStartController();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = uiFactory.createTitle("START GAME");
        title.setFontScale(2.5f);

        messageLabel = new Label(L10n.tr("Choose Load Game, New Game, or Delete Save first."), skin);
        messageLabel.setColor(Color.LIGHT_GRAY);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true);
        messageLabel.setFontScale(1.2f);

        Table modeButtons = new Table();
        addModeButtons(modeButtons);

        Table slotContent = new Table();
        slotContent.defaults().pad(6f);

        createConfirmationBox(slotContent);
        addSaveSlotButtons(slotContent);

        ScrollPane slotScrollPane = new ScrollPane(slotContent, skin);
        slotScrollPane.setFadeScrollBars(false);
        slotScrollPane.setScrollingDisabled(true, false);
        slotScrollPane.setCancelTouchFocus(false);
        slotScrollPane.setFlickScroll(false);

        TextButton backButton = uiFactory.createButton(
            "Back",
            () -> game.getScreenManager().show(ScreenType.MAIN_MENU)
        );

        root.add(title)
            .padTop(10f)
            .padBottom(8f)
            .row();

        root.add(messageLabel)
            .width(700f)
            .height(36f)
            .padBottom(6f)
            .row();

        root.add(modeButtons)
            .width(760f)
            .height(60f)
            .padBottom(6f)
            .row();

        root.add(slotScrollPane)
            .width(760f)
            .height(310f)
            .padBottom(6f)
            .row();

        root.add(backButton)
            .width(300f)
            .height(48f)
            .padBottom(6f)
            .row();
    }

    private void addModeButtons(Table table) {
        TextButton loadButton = uiFactory.createButton("Load Game", this::enterLoadGameMode);
        TextButton newGameButton = uiFactory.createButton("New Game", this::enterNewGameMode);
        TextButton deleteButton = uiFactory.createButton("Delete Save", this::enterDeleteMode);

        table.add(loadButton)
            .width(230f)
            .height(52f)
            .pad(6f);

        table.add(newGameButton)
            .width(230f)
            .height(52f)
            .pad(6f);

        table.add(deleteButton)
            .width(230f)
            .height(52f)
            .pad(6f)
            .row();
    }

    private void addSaveSlotButtons(Table table) {
        for (SaveSlot saveSlot : controller.getSaveSlots()) {
            TextButton slotButton = uiFactory.createButton(
                getSlotButtonText(saveSlot),
                () -> handleSlotClick(saveSlot.getSlotNumber())
            );

            slotButton.getLabel().setWrap(true);
            slotButton.getLabel().setAlignment(Align.center);
            slotButton.getLabel().setFontScale(1f);

            table.add(slotButton)
                .width(580f)
                .height(92f)
                .pad(7f)
                .row();
        }
    }

    private String getSlotButtonText(SaveSlot saveSlot) {
        if (!saveSlot.isOccupied()) {
            return (L10n.isFrench() ? "Emplacement " : "Save Slot ") + saveSlot.getSlotNumber()
                + "\n" + L10n.tr("EMPTY");
        }

        String statusText = saveSlot.isGameCompleted()
            ? L10n.tr("COMPLETED")
            : L10n.areaName(saveSlot.getAreaName());

        return (L10n.isFrench() ? "Emplacement " : "Save Slot ") + saveSlot.getSlotNumber()
            + "\n" + statusText
            + "\nHP " + saveSlot.getPlayerHealth()
            + (L10n.isFrench() ? " | Ame " : " | Soul ") + saveSlot.getPlayerSoul()
            + (L10n.isFrench() ? " | Morts " : " | Deaths ") + saveSlot.getDeathCount();
    }

    private void createConfirmationBox(Table parentTable) {
        confirmationBox = new Table(skin);
        confirmationBox.setVisible(false);

        confirmationLabel = new Label("", skin);
        confirmationLabel.setColor(Color.ORANGE);
        confirmationLabel.setWrap(true);
        confirmationLabel.setAlignment(Align.center);
        confirmationLabel.setFontScale(1.2f);

        TextButton yesButton = uiFactory.createButton("Yes, Delete", this::confirmPendingDelete);
        TextButton noButton = uiFactory.createButton("No", this::cancelPendingDelete);

        confirmationBox.add(confirmationLabel)
            .colspan(2)
            .width(560f)
            .height(44f)
            .padBottom(4f)
            .row();

        confirmationBox.add(yesButton)
            .width(190f)
            .height(42f)
            .padRight(8f);

        confirmationBox.add(noButton)
            .width(190f)
            .height(42f)
            .padLeft(8f)
            .row();

        parentTable.add(confirmationBox)
            .width(600f)
            .height(100f)
            .padBottom(6f)
            .row();
    }

    private void enterLoadGameMode() {
        selectionMode = SlotSelectionMode.LOAD_GAME;
        hideDeleteConfirmation();
        showInfo("Choose a save slot to load.");
    }

    private void enterNewGameMode() {
        selectionMode = SlotSelectionMode.CREATE_NEW_GAME;
        hideDeleteConfirmation();
        showInfo("Choose an empty save slot for your new game.");
    }

    private void enterDeleteMode() {
        selectionMode = SlotSelectionMode.DELETE_SAVE;
        hideDeleteConfirmation();
        showWarning("Choose a save slot to delete.");
    }

    private void handleSlotClick(int slotNumber) {
        if (selectionMode == SlotSelectionMode.NONE) {
            showError("First choose Load Game, New Game, or Delete Save.");
            return;
        }

        if (selectionMode == SlotSelectionMode.CREATE_NEW_GAME) {
            createNewGame(slotNumber);
            return;
        }

        if (selectionMode == SlotSelectionMode.DELETE_SAVE) {
            askDeleteConfirmation(slotNumber);
            return;
        }

        loadGame(slotNumber);
    }

    private void createNewGame(int slotNumber) {
        hideDeleteConfirmation();

        if (!controller.canCreateNewGameInSlot(slotNumber)) {
            showError("Save slot " + slotNumber + " is already used.");
            return;
        }

        controller.startNewGameInSlot(
            slotNumber,
            game.getPlayerStats(),
            game.getInventoryController()
        );

        game.getAchievementController().loadFromSave(
            controller.getCurrentSlotAchievementNames()
        );

        game.getScreenManager().show(ScreenType.GAME);
    }

    private void loadGame(int slotNumber) {
        hideDeleteConfirmation();

        if (!controller.canLoadSlot(slotNumber)) {
            showError("Save slot " + slotNumber + " is empty.");
            return;
        }

        controller.loadGameFromSlot(
            slotNumber,
            game.getPlayerStats(),
            game.getInventoryController()
        );

        game.getAchievementController().loadFromSave(
            controller.getCurrentSlotAchievementNames()
        );

        SaveSlot slot = controller.getSlot(slotNumber);

        if (slot.isGameCompleted()) {
            game.saveLastRunStats(
                slot.getElapsedTimeSeconds(),
                slot.getDefeatedEnemyNames().size(),
                slot.getDeathCount()
            );

            game.getScreenManager().show(ScreenType.VICTORY);
            return;
        }

        game.getScreenManager().show(ScreenType.GAME);
    }

    private void askDeleteConfirmation(int slotNumber) {
        if (!controller.canDeleteSlot(slotNumber)) {
            hideDeleteConfirmation();
            showError("Save slot " + slotNumber + " is already empty.");
            return;
        }

        pendingDeleteSlotNumber = slotNumber;

        confirmationLabel.setText(
            L10n.dynamic(
                "Delete Save Slot " + slotNumber
                    + "?\nThis action cannot be undone."
            )
        );

        confirmationBox.setVisible(true);
        showWarning("Confirm delete or cancel.");
    }

    private void confirmPendingDelete() {
        if (pendingDeleteSlotNumber == NO_PENDING_DELETE_SLOT) {
            hideDeleteConfirmation();
            showError("No save slot selected for deletion.");
            return;
        }

        controller.deleteSlot(pendingDeleteSlotNumber);

        game.getAchievementController().loadFromSave(
            controller.getCurrentSlotAchievementNames()
        );

        pendingDeleteSlotNumber = NO_PENDING_DELETE_SLOT;
        selectionMode = SlotSelectionMode.NONE;

        game.getScreenManager().show(ScreenType.GAME_START);
    }

    private void cancelPendingDelete() {
        hideDeleteConfirmation();
        showInfo("Delete cancelled.");
    }

    private void hideDeleteConfirmation() {
        pendingDeleteSlotNumber = NO_PENDING_DELETE_SLOT;

        if (confirmationBox != null) {
            confirmationBox.setVisible(false);
        }
    }

    private void showInfo(String message) {
        messageLabel.setColor(Color.LIGHT_GRAY);
        messageLabel.setText(L10n.dynamic(message));
    }

    private void showWarning(String message) {
        messageLabel.setColor(Color.ORANGE);
        messageLabel.setText(L10n.dynamic(message));
    }

    private void showError(String message) {
        messageLabel.setColor(Color.RED);
        messageLabel.setText(L10n.dynamic(message));
    }

    private enum SlotSelectionMode {
        NONE,
        LOAD_GAME,
        CREATE_NEW_GAME,
        DELETE_SAVE
    }

    @Override
    protected String getBackgroundPath() {
        return "backgrounds/menu/start_game.jpg";
    }
}
