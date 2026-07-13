package io.github.some_example_name.controller.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.some_example_name.Main;
import io.github.some_example_name.controller.audio.AudioManager;
import io.github.some_example_name.model.achievement.AchievementType;
import io.github.some_example_name.model.game.GameWorld;
import io.github.some_example_name.model.game.NailAttack;
import io.github.some_example_name.model.game.Player;
import io.github.some_example_name.model.game.Zote;
import io.github.some_example_name.model.input.GameInputState;
import io.github.some_example_name.util.CollisionUtils;
import io.github.some_example_name.view.ui.ZoteDialogueOverlay;
import io.github.some_example_name.util.L10n;

public class GameZoteInteractionController {
    private static final float INTERACTION_DISTANCE =
        112f;

    private static final float ATTACK_REACTION_TIME =
        0.50f;

    private static final String[] DEFAULT_LINES = {
        "You stand before Zote the Mighty, a knight of great renown.",
        "Do not confuse my patience with weakness. I have defeated beasts far worse than you.",
        "Remember this precept: never trust a silent corridor. It is usually waiting to embarrass you."
    };

    private static final String[] AFTER_BOSS_LINES = {
        "So, the armoured fool has fallen. Naturally, my presence inspired your victory.",
        "A lesser warrior might celebrate. Zote the Mighty simply continues onward.",
        "Precept Fifty-Seven: claim credit quickly, before someone else does."
    };

    private static final String[] DEFAULT_LINES_FR = {
        "Vous vous tenez devant Zote le Puissant, chevalier de grande renommee.",
        "Ne confondez pas ma patience avec de la faiblesse. J'ai vaincu des betes bien pires que vous.",
        "Souvenez-vous de ce precepte : ne faites jamais confiance a un couloir silencieux."
    };

    private static final String[] AFTER_BOSS_LINES_FR = {
        "Ainsi, l'idiot en armure est tombe. Ma presence a naturellement inspire votre victoire.",
        "Un guerrier inferieur celebrerait. Zote le Puissant poursuit simplement sa route.",
        "Precepte cinquante-sept : revendiquez vite le merite avant qu'un autre ne le fasse."
    };

    private static final String[] REPEAT_PRECEPTS = {
        "Precept Four: forget your past. It has already forgotten you.",
        "Precept Twelve: keep your weapon polished. Enemies respect unnecessary shine.",
        "Precept Twenty-Nine: if a path looks dangerous, declare it beneath you.",
        "Precept Forty-One: a dramatic pause improves every heroic statement.",
        "Precept Sixty-Two: never admit you were lost. Say the kingdom moved."
    };

    private static final String[] REPEAT_PRECEPTS_FR = {
        "Precepte quatre : oubliez votre passe. Il vous a deja oublie.",
        "Precepte douze : gardez votre arme brillante. Les ennemis respectent l'eclat inutile.",
        "Precepte vingt-neuf : si un chemin semble dangereux, declarez-le indigne de vous.",
        "Precepte quarante-et-un : une pause dramatique ameliore toute declaration heroique.",
        "Precepte soixante-deux : n'avouez jamais etre perdu. Dites que le royaume a bouge."
    };

    private final GameWorld world;
    private final Main game;
    private final AudioManager audioManager;
    private final GameMessageController messageController;
    private final ZoteDialogueOverlay overlay;

    private int dialogueIndex;
    private int completedConversations;
    private boolean talking;
    private String[] activeLines = DEFAULT_LINES;
    private NailAttack lastProcessedAttack;

    public GameZoteInteractionController(
        GameWorld world,
        Main game,
        Skin skin,
        Stage stage,
        GameMessageController messageController
    ) {
        this.world = world;
        this.game = game;
        this.audioManager = game.getAudioManager();
        this.messageController = messageController;
        this.overlay = new ZoteDialogueOverlay(
            skin,
            stage
        );
    }

    public boolean update(
        float delta,
        GameInputState inputState
    ) {
        Zote zote = world.getZote().orElse(null);

        if (zote == null) {
            closeDialogue();
            return false;
        }

        zote.update(delta);
        checkAttackReaction(zote);

        if (talking) {
            stopPlayerMovement();

            if (
                inputState.isDialogueNextJustPressed()
                    || inputState.isInteractJustPressed()
            ) {
                advanceDialogue(zote);
            }

            return true;
        }

        if (
            inputState.isInteractJustPressed()
                && isPlayerNear(zote)
        ) {
            beginDialogue(zote);
            return true;
        }

        return false;
    }

    public void position(Stage stage) {
        overlay.position(stage);
    }

    public void dispose() {
        audioManager.stopCurrentZoteVoice();
        overlay.dispose();
    }

    private void beginDialogue(Zote zote) {
        talking = true;
        dialogueIndex = 0;
        activeLines = chooseDialogueLines();
        zote.setTalking(true);

        stopPlayerMovement();
        showCurrentLine();
    }

    private void advanceDialogue(Zote zote) {
        dialogueIndex++;

        if (dialogueIndex >= activeLines.length) {
            completedConversations++;
            talking = false;
            zote.setTalking(false);
            overlay.hideDialogue();
            audioManager.stopCurrentZoteVoice();
            return;
        }

        showCurrentLine();
    }

    private void showCurrentLine() {
        overlay.showLine(activeLines[dialogueIndex]);
        audioManager.playRandomZoteVoice();
    }

    private String[] chooseDialogueLines() {
        boolean bossDefeated = game.getAchievementController()
            .isUnlocked(AchievementType.DEFEAT_FALSE_KNIGHT);

        if (bossDefeated) {
            return L10n.isFrench() ? AFTER_BOSS_LINES_FR : AFTER_BOSS_LINES;
        }

        if (completedConversations == 0) {
            return L10n.isFrench() ? DEFAULT_LINES_FR : DEFAULT_LINES;
        }

        String[] source = L10n.isFrench() ? REPEAT_PRECEPTS_FR : REPEAT_PRECEPTS;
        String[] selection = new String[3];
        int startIndex = (completedConversations - 1) % source.length;
        for (int index = 0; index < selection.length; index++) {
            selection[index] = source[(startIndex + index) % source.length];
        }
        return selection;
    }

    private void checkAttackReaction(Zote zote) {
        NailAttack attack =
            world.getActiveNailAttack().orElse(null);

        if (attack == null) {
            lastProcessedAttack = null;
            return;
        }

        if (attack == lastProcessedAttack) {
            return;
        }

        boolean hit = CollisionUtils.overlaps(
            attack.getX(),
            attack.getY(),
            attack.getWidth(),
            attack.getHeight(),
            zote.getX(),
            zote.getY(),
            zote.getWidth(),
            zote.getHeight()
        );

        if (!hit) {
            return;
        }

        lastProcessedAttack = attack;
        zote.startAttackReaction(
            ATTACK_REACTION_TIME,
            world.getPlayer().getCenterX()
        );
        audioManager.playRandomZoteVoice();
        messageController.showTemporaryMessage(
            "Zote: Your attack is beneath my notice."
        );
    }

    private boolean isPlayerNear(Zote zote) {
        Player player = world.getPlayer();

        float dx =
            player.getCenterX() - zote.getCenterX();
        float dy =
            player.getCenterY() - zote.getCenterY();

        return dx * dx + dy * dy
            <= INTERACTION_DISTANCE
            * INTERACTION_DISTANCE;
    }

    private void stopPlayerMovement() {
        Player player = world.getPlayer();

        player.setVelocityX(0f);
        player.setVelocityY(0f);
        player.stopDash();
        player.clearKnockback();
    }

    private void closeDialogue() {
        if (!talking) {
            return;
        }

        talking = false;
        overlay.hideDialogue();
        audioManager.stopCurrentZoteVoice();
    }
}
