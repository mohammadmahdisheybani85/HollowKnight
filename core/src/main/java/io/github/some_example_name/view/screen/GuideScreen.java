package io.github.some_example_name.view.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.controller.input.KeyBindingController;
import io.github.some_example_name.model.input.PlayerAction;
import io.github.some_example_name.util.L10n;

public class GuideScreen extends AbstractMenuScreen {
    private KeyBindingController keyBindingController;

    public GuideScreen(Main game) {
        super(game);
    }

    @Override
    protected void buildUI() {
        keyBindingController = game.getKeyBindingController();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = uiFactory.createTitle("GUIDE");
        title.setFontScale(1.3f);

        Table content = new Table();
        content.defaults().pad(4f);

        buildGuideContent(content);

        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        root.add(title)
            .padTop(12f)
            .padBottom(10f)
            .row();

        root.add(scrollPane)
            .width(760f)
            .height(430f)
            .padBottom(10f)
            .row();

        root.add(uiFactory.createBackButton(
                () -> game.getScreenManager().show(ScreenType.MAIN_MENU)
            ))
            .width(300f)
            .height(54f)
            .padBottom(8f)
            .row();
    }

    private void buildGuideContent(Table table) {
        addSectionTitle(table, "Controls");
        addControlRow(table, PlayerAction.MOVE_LEFT);
        addControlRow(table, PlayerAction.MOVE_RIGHT);
        addControlRow(table, PlayerAction.LOOK_UP);
        addControlRow(table, PlayerAction.LOOK_DOWN);
        addControlRow(table, PlayerAction.JUMP);
        addControlRow(table, PlayerAction.DASH);
        addControlRow(table, PlayerAction.ATTACK);
        addControlRow(table, PlayerAction.CAST_SPELL);
        addControlRow(table, PlayerAction.FOCUS);
        addControlRow(table, PlayerAction.INTERACT);
        addControlRow(table, PlayerAction.OPEN_INVENTORY);
        addControlRow(table, PlayerAction.PAUSE);

        addTextRow(
            table,
            (L10n.isFrench() ? "Hurlements des Spectres : " : "Howling Wraiths: ")
                + keyBindingController.getKeyName(PlayerAction.LOOK_UP)
                + " + "
                + keyBindingController.getKeyName(PlayerAction.CAST_SPELL)
        );

        addSectionTitle(table, "Knight Abilities");
        addTextRow(table, "HP shows how much damage the Knight can take.");
        addTextRow(table, "Soul fills when the Knight hits enemies.");
        addTextRow(table, "Vengeful Spirit: spend 33 Soul to cast a forward spell.");
        addTextRow(table, "Howling Wraiths: spend 33 Soul to attack upward.");
        addTextRow(table, (L10n.isFrench() ? "Concentration : maintenez " : "Focus: hold ") + keyBindingController.getKeyName(PlayerAction.FOCUS) + (L10n.isFrench() ? " au sol pour depenser 33 d'ame et restaurer 1 PV." : " on the ground to spend 33 Soul and heal 1 HP."));
        addTextRow(table, "Dash helps you cross gaps and avoid enemies.");
        addTextRow(table, "Double Jump: press Jump again while airborne.");
        addTextRow(table, "Wall Slide: hold toward a wall while falling.");
        addTextRow(table, "Pogo: hold Look Down and press Attack above an enemy or spikes.");
        addTextRow(table, (L10n.isFrench() ? "Interagir : appuyez sur " : "Interact: press ")
            + keyBindingController.getKeyName(PlayerAction.INTERACT)
            + (L10n.isFrench() ? " pres de Zote, puis utilisez ENTREE ou la touche d'interaction pour continuer." : " near Zote, then use ENTER or the interact key to continue dialogue."));

        addSectionTitle(table, "Game Goal");
        addTextRow(table, "Defeat False Knight to complete the game.");
        addTextRow(table, "You may skip normal enemies, but hunting them helps achievements.");
        addTextRow(table, "Victory saves the slot as COMPLETED.");

        addSectionTitle(table, "Achievements");
        addTextRow(table, "Completion: finish the game.");
        addTextRow(table, "Speedrun: finish under the time limit.");
        addTextRow(table, "True Hunter: defeat every required enemy type.");
        addTextRow(table, "Defeat False Knight: defeat the boss.");
        addTextRow(table, "Soul Master: fill the Soul orb completely.");

        addSectionTitle(table, "Cheat Codes");
        addTextRow(table, "GODMODE - Toggle infinite health.");
        addTextRow(table, "FULLSOUL - Fill the Soul bar.");
        addTextRow(table, "BOSSTELEPORT - Teleport to the boss arena.");
        addTextRow(table, "NOCLIP - Toggle collision-free spectator movement.");
        addTextRow(table, "HEAL - Restore all health.");
        addTextRow(table, "INSTAKILL - Defeat every active enemy.");
        addTextRow(table, "Type cheat codes during gameplay.");
    }

    private void addSectionTitle(Table table, String title) {
        Label label = new Label(L10n.tr(title), skin);
        label.setFontScale(1.08f);
        label.setColor(0.70f, 0.82f, 1f, 1f);
        label.setAlignment(Align.center);

        table.add(label)
            .width(650f)
            .padTop(14f)
            .padBottom(6f)
            .row();
    }

    private void addControlRow(Table table, PlayerAction action) {
        String text = L10n.actionName(action) + ": " + keyBindingController.getKeyName(action);
        addTextRow(table, text);
    }

    private void addTextRow(Table table, String text) {
        Label label = new Label(L10n.dynamic(text), skin);
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setFontScale(0.86f);

        table.add(label)
            .width(650f)
            .pad(4f)
            .row();
    }

    @Override
    protected String getBackgroundPath() {
        return "backgrounds/menu/guide.jpg";
    }
}
