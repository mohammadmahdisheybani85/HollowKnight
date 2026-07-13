package io.github.some_example_name.controller.game;

public class GameAreaTransitionFeedbackController {
    private final GameAreaTransitionController areaTransitionController;
    private final GameMessageController gameMessageController;
    private final GameAreaTitleController areaTitleController;

    public GameAreaTransitionFeedbackController(
        GameAreaTransitionController areaTransitionController,
        GameMessageController gameMessageController,
        GameAreaTitleController areaTitleController
    ) {
        this.areaTransitionController = areaTransitionController;
        this.gameMessageController = gameMessageController;
        this.areaTitleController = areaTitleController;
    }

    public void update() {
        String areaName = areaTransitionController.moveToNextAreaIfNeeded();

        if (areaName.isEmpty()) {
            return;
        }

        gameMessageController.showTemporaryMessage("Entered " + areaName);
        areaTitleController.showArea(areaName);
    }
}
