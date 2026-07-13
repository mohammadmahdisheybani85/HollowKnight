package io.github.some_example_name.util;

import io.github.some_example_name.controller.settings.SettingsController;
import io.github.some_example_name.model.achievement.Achievement;
import io.github.some_example_name.model.achievement.AchievementType;
import io.github.some_example_name.model.game.WorldArea;
import io.github.some_example_name.model.input.PlayerAction;
import io.github.some_example_name.model.inventory.Charm;
import io.github.some_example_name.model.inventory.CharmType;

public final class L10n {
    private static SettingsController settingsController;

    private L10n() {
    }

    public static void initialize(SettingsController controller) {
        settingsController = controller;
    }

    public static boolean isFrench() {
        return settingsController != null
            && "fr".equals(settingsController.getLanguageCode());
    }

    public static String tr(String english) {
        if (!isFrench() || english == null) {
            return english;
        }

        return switch (english) {
            case "START GAME" -> "COMMENCER";
            case "SETTINGS" -> "PARAMETRES";
            case "GUIDE" -> "GUIDE";
            case "ACHIEVEMENTS" -> "SUCCES";
            case "QUIT GAME" -> "QUITTER";
            case "Back", "BACK" -> "RETOUR";
            case "Load Game" -> "CHARGER";
            case "New Game" -> "NOUVELLE PARTIE";
            case "Delete Save" -> "SUPPRIMER LA SAUVEGARDE";
            case "Yes, Delete" -> "OUI, SUPPRIMER";
            case "No", "NO" -> "NON";
            case "YES" -> "OUI";
            case "EMPTY" -> "VIDE";
            case "COMPLETED" -> "TERMINE";
            case "VICTORY" -> "VICTOIRE";
            case "Achievements" -> "Succes";
            case "Main Menu" -> "Menu principal";
            case "Restart" -> "Recommencer";
            case "Run Statistics" -> "Statistiques de la partie";
            case "Status: Completed" -> "Statut : termine";
            case "Pause Menu" -> "Menu pause";
            case "Continue" -> "Continuer";
            case "Show Cheat Codes" -> "Afficher les codes de triche";
            case "Hide Cheat Codes" -> "Masquer les codes de triche";
            case "Settings" -> "Parametres";
            case "Save & Quit" -> "Sauvegarder et quitter";
            case "Pause Settings" -> "Parametres de pause";
            case "Music Volume" -> "Volume musique";
            case "SFX Volume" -> "Volume effets";
            case "Brightness" -> "Luminosite";
            case "Mute Music" -> "Couper la musique";
            case "Mute SFX" -> "Couper les effets";
            case "Test SFX" -> "Tester les effets";
            case "Reset Sounds" -> "Reinitialiser les sons";
            case "Back to Pause" -> "Retour a la pause";
            case "Inventory" -> "Inventaire";
            case "Controls" -> "Commandes";
            case "Knight Abilities" -> "Capacites du chevalier";
            case "Game Goal" -> "Objectif du jeu";
            case "Cheat Codes" -> "Codes de triche";
            case "Achievement Unlocked!" -> "Succes debloque !";
            case "FALSE KNIGHT" -> "FAUX CHEVALIER";
            case "False Knight" -> "Faux Chevalier";
            case "Forgotten Crossroads" -> "Routes Oubliees";
            case "Greenpath" -> "Vertchemin";
            case "False Knight Arena" -> "Arene du Faux Chevalier";
            case "SOUL" -> "AME";
            case "GOD MODE" -> "MODE DIVIN";
            case "INVINCIBLE" -> "INVINCIBLE";
            case "ZOTE" -> "ZOTE";
            case "Continue dialogue" -> "Continuer le dialogue";
            case "Click a charm to equip or unequip it." ->
                "Cliquez sur un charme pour l'equiper ou le retirer.";
            case "Not enough charm notches." ->
                "Pas assez d'encoches de charme.";
            case "This charm is hidden in a secret room." ->
                "Ce charme est cache dans une salle secrete.";
            case "Find this charm in a secret room." ->
                "Trouvez ce charme dans une salle secrete.";
            case "Controls and language can be changed\nfrom Main Menu Settings." ->
                "Les commandes et la langue peuvent etre modifiees\ndans les parametres du menu principal.";
            case "GODMODE - Toggle infinite health\nFULLSOUL - Fill Soul bar" ->
                "GODMODE - Activer la vie infinie\nFULLSOUL - Remplir la jauge d'ame";
            case "Type cheat codes during gameplay." ->
                "Tapez les codes de triche pendant la partie.";
            case "GODMODE - Toggle infinite health." ->
                "GODMODE - Activer la vie infinie.";
            case "FULLSOUL - Fill the Soul bar." ->
                "FULLSOUL - Remplir la jauge d'ame.";
            case "BOSSTELEPORT - Teleport to the boss arena." ->
                "BOSSTELEPORT - Se teleporter dans l'arene du boss.";
            case "NOCLIP - Toggle collision-free spectator movement." ->
                "NOCLIP - Activer le deplacement spectateur sans collision.";
            case "HEAL - Restore all health." ->
                "HEAL - Restaurer toute la sante.";
            case "INSTAKILL - Defeat every active enemy." ->
                "INSTAKILL - Vaincre tous les ennemis actifs.";
            case "GODMODE - Toggle infinite health\nFULLSOUL - Fill Soul bar\nBOSSTELEPORT - Boss arena\nNOCLIP - Spectator mode\nHEAL - Restore health\nINSTAKILL - Defeat enemies" ->
                "GODMODE - Vie infinie\nFULLSOUL - Remplir l'ame\nBOSSTELEPORT - Arene du boss\nNOCLIP - Mode spectateur\nHEAL - Restaurer la sante\nINSTAKILL - Vaincre les ennemis";
            case "False Knight has been defeated." ->
                "Le Faux Chevalier a ete vaincu.";
            case "Hallownest is one step safer." ->
                "Hallownest est un peu plus sure.";
            case "Choose Load Game, New Game, or Delete Save first." ->
                "Choisissez d'abord Charger, Nouvelle partie ou Supprimer.";
            case "Choose a save slot to load." ->
                "Choisissez un emplacement a charger.";
            case "Choose an empty save slot for your new game." ->
                "Choisissez un emplacement vide pour la nouvelle partie.";
            case "Choose a save slot to delete." ->
                "Choisissez un emplacement a supprimer.";
            case "First choose Load Game, New Game, or Delete Save." ->
                "Choisissez d'abord Charger, Nouvelle partie ou Supprimer.";
            case "Confirm delete or cancel." ->
                "Confirmez la suppression ou annulez.";
            case "Delete cancelled." -> "Suppression annulee.";
            case "No save slot selected for deletion." ->
                "Aucun emplacement selectionne pour la suppression.";
            case "This action cannot be undone." ->
                "Cette action est irreversible.";
            case "Not enough Soul." -> "Pas assez d'ame.";
            case "Focusing..." -> "Concentration...";
            case "Healed!" -> "Soigne !";
            case "Spell is cooling down." -> "Le sort se recharge.";
            case "Vengeful Spirit!" -> "Esprit Vengeur !";
            case "Howling Wraiths!" -> "Hurlements des Spectres !";
            case "Enemy hit!" -> "Ennemi touche !";
            case "Hazard damage!" -> "Degats de piege !";
            case "You died." -> "Vous etes mort.";
            case "You fell into a pit." -> "Vous etes tombe dans un gouffre.";
            case "GODMODE activated." -> "GODMODE active.";
            case "GODMODE deactivated." -> "GODMODE desactive.";
            case "Soul bar filled." -> "Jauge d'ame remplie.";
            case "Teleported to Boss Arena." -> "Teleporte dans l'arene du boss.";
            case "NOCLIP activated." -> "NOCLIP active.";
            case "NOCLIP deactivated." -> "NOCLIP desactive.";
            case "Health restored." -> "Sante restauree.";
            case "All enemies defeated." -> "Tous les ennemis sont vaincus.";
            case "Boss attack!" -> "Attaque du boss !";
            case "Shockwave!" -> "Onde de choc !";
            case "Zote: Your attack is beneath my notice." ->
                "Zote : Votre attaque ne merite meme pas mon attention.";
            case "HP shows how much damage the Knight can take." ->
                "Les PV indiquent combien de degats le chevalier peut subir.";
            case "Soul fills when the Knight hits enemies." ->
                "L'ame se remplit lorsque le chevalier frappe les ennemis.";
            case "Vengeful Spirit: spend 33 Soul to cast a forward spell." ->
                "Esprit Vengeur : depensez 33 d'ame pour lancer un sort vers l'avant.";
            case "Howling Wraiths: spend 33 Soul to attack upward." ->
                "Hurlements des Spectres : depensez 33 d'ame pour attaquer vers le haut.";
            case "Dash helps you cross gaps and avoid enemies." ->
                "La ruee aide a franchir les fosses et eviter les ennemis.";
            case "Double Jump: press Jump again while airborne." ->
                "Double saut : appuyez de nouveau sur Saut dans les airs.";
            case "Wall Slide: hold toward a wall while falling." ->
                "Glissade murale : maintenez la direction du mur pendant la chute.";
            case "Pogo: hold Look Down and press Attack above an enemy or spikes." ->
                "Pogo : regardez en bas et attaquez au-dessus d'un ennemi ou de pointes.";
            case "Defeat False Knight to complete the game." ->
                "Battez le Faux Chevalier pour terminer le jeu.";
            case "You may skip normal enemies, but hunting them helps achievements." ->
                "Vous pouvez eviter les ennemis ordinaires, mais les chasser aide pour les succes.";
            case "Victory saves the slot as COMPLETED." ->
                "La victoire marque l'emplacement comme TERMINE.";
            case "Completion: finish the game." ->
                "Accomplissement : terminez le jeu.";
            case "Speedrun: finish under the time limit." ->
                "Course rapide : terminez avant la limite de temps.";
            case "True Hunter: defeat every required enemy type." ->
                "Vrai chasseur : battez chaque type d'ennemi requis.";
            case "Defeat False Knight: defeat the boss." ->
                "Vaincre le Faux Chevalier : battez le boss.";
            case "Soul Master: fill the Soul orb completely." ->
                "Maitre de l'ame : remplissez completement l'orbe d'ame.";
            default -> english;
        };
    }

    public static String dynamic(String text) {
        if (!isFrench() || text == null || text.isBlank()) {
            return text;
        }

        String exact = tr(text);
        if (!exact.equals(text)) {
            return exact;
        }

        if (text.startsWith("Elapsed Time: ")) {
            return "Temps ecoule : " + text.substring("Elapsed Time: ".length());
        }
        if (text.startsWith("Enemies Killed: ")) {
            return "Ennemis vaincus : " + text.substring("Enemies Killed: ".length());
        }
        if (text.startsWith("Deaths: ")) {
            return "Morts : " + text.substring("Deaths: ".length());
        }
        if (text.startsWith("You died. Deaths: ")) {
            return "Vous etes mort. Morts : " + text.substring("You died. Deaths: ".length());
        }
        if (text.startsWith("Entered ")) {
            return "Entree dans " + areaName(text.substring("Entered ".length()));
        }
        if (text.startsWith("Delete Save Slot ")) {
            return text.replace("Delete Save Slot ", "Supprimer l'emplacement ")
                .replace("This action cannot be undone.", "Cette action est irreversible.");
        }
        if (text.startsWith("Save slot ") && text.endsWith(" is already used.")) {
            return text.replace("Save slot ", "L'emplacement ")
                .replace(" is already used.", " est deja utilise.");
        }
        if (text.startsWith("Save slot ") && text.endsWith(" is empty.")) {
            return text.replace("Save slot ", "L'emplacement ")
                .replace(" is empty.", " est vide.");
        }
        if (text.startsWith("Save slot ") && text.endsWith(" is already empty.")) {
            return text.replace("Save slot ", "L'emplacement ")
                .replace(" is already empty.", " est deja vide.");
        }
        if (text.startsWith("Charm Notches: ")) {
            return "Encoches de charme : " + text.substring("Charm Notches: ".length());
        }
        if (text.startsWith("SOUL: ")) {
            return "AME : " + text.substring("SOUL: ".length());
        }
        if (text.startsWith("GOD MODE: ")) {
            return "MODE DIVIN : " + translateYesNo(text.substring("GOD MODE: ".length()));
        }
        if (text.startsWith("INVINCIBLE: ")) {
            return "INVINCIBLE : " + translateYesNo(text.substring("INVINCIBLE: ".length()));
        }

        return text;
    }

    public static String actionName(PlayerAction action) {
        if (!isFrench()) {
            return action.getDisplayName();
        }

        return switch (action) {
            case MOVE_LEFT -> "Deplacement gauche";
            case MOVE_RIGHT -> "Deplacement droite";
            case LOOK_UP -> "Regarder en haut / sort vertical";
            case LOOK_DOWN -> "Regarder en bas / pogo";
            case JUMP -> "Saut";
            case DASH -> "Ruee";
            case ATTACK -> "Attaque";
            case CAST_SPELL -> "Lancer un sort";
            case FOCUS -> "Concentration / soin";
            case INTERACT -> "Interagir";
            case OPEN_INVENTORY -> "Ouvrir l'inventaire";
            case PAUSE -> "Pause";
        };
    }

    public static String achievementText(Achievement achievement) {
        String title = achievementTitle(achievement.getType());
        if (achievement.isUnlocked()) {
            return isFrench()
                ? "[DEBLOQUE] " + title + " - " + achievementDescription(achievement.getType())
                : achievement.getDisplayText();
        }
        return isFrench() ? "[VERROUILLE] " + title : achievement.getDisplayText();
    }

    public static String achievementTitle(AchievementType type) {
        if (!isFrench()) return type.getTitle();
        return switch (type) {
            case COMPLETION -> "Accomplissement";
            case SPEEDRUN -> "Course rapide";
            case TRUE_HUNTER -> "Vrai chasseur";
            case DEFEAT_FALSE_KNIGHT -> "Vaincre le Faux Chevalier";
            case SOUL_MASTER -> "Maitre de l'ame";
        };
    }

    public static String achievementDescription(AchievementType type) {
        if (!isFrench()) return type.getDescription();
        return switch (type) {
            case COMPLETION -> "Terminez le jeu.";
            case SPEEDRUN -> "Terminez le jeu dans le temps imparti.";
            case TRUE_HUNTER -> "Battez chaque type d'ennemi.";
            case DEFEAT_FALSE_KNIGHT -> "Battez le boss Faux Chevalier.";
            case SOUL_MASTER -> "Remplissez completement l'orbe d'ame.";
        };
    }

    public static String charmText(Charm charm) {
        String status = charm.isEquipped()
            ? (isFrench() ? "[EQUIPE] " : "[EQUIPPED] ")
            : "[ ] ";
        String notchWord = isFrench() ? " encoches" : " notches";
        return status + charmTitle(charm.getType())
            + " (" + charm.getType().getNotchCost() + notchWord + ")";
    }

    public static String charmTitle(CharmType type) {
        if (!isFrench()) return type.getTitle();
        return switch (type) {
            case SOUL_CATCHER -> "Attrape-ame";
            case DASH_MASTER -> "Maitre de la ruee";
            case UNBREAKABLE_STRENGTH -> "Force incassable";
            case QUICK_SLASH -> "Entaille rapide";
            case QUICK_FOCUS -> "Concentration rapide";
            case HEAVY_BLOW -> "Coup puissant";
            case SHARP_SHADOW -> "Ombre tranchante";
            case VOID_HEART -> "Coeur du vide";
            case STEADY_BODY -> "Corps stable";
            case VITALITY_CHARM -> "Charme de vitalite";
        };
    }

    public static String charmDescription(CharmType type) {
        if (!isFrench()) return type.getDescription();
        return switch (type) {
            case SOUL_CATCHER -> "Gagne plus d'ame en frappant les ennemis.";
            case DASH_MASTER -> "Reduit le delai de la ruee.";
            case UNBREAKABLE_STRENGTH -> "Augmente les degats du clou de cinquante pour cent.";
            case QUICK_SLASH -> "Permet des attaques de clou plus rapides.";
            case QUICK_FOCUS -> "Soigne plus rapidement avec la concentration.";
            case HEAVY_BLOW -> "Augmente fortement le recul des ennemis.";
            case SHARP_SHADOW -> "Traverse les ennemis, inflige des degats et allonge la ruee.";
            case VOID_HEART -> "Augmente les degats des sorts de cinquante pour cent.";
            case STEADY_BODY -> "Reduit le recul apres avoir subi des degats.";
            case VITALITY_CHARM -> "Ajoute un masque de sante.";
        };
    }

    public static String areaName(WorldArea area) {
        if (area == null) return "";
        return areaName(area.getDisplayName());
    }

    public static String areaName(String englishName) {
        if (!isFrench()) return englishName;
        return switch (englishName) {
            case "Forgotten Crossroads" -> "Routes Oubliees";
            case "Greenpath" -> "Vertchemin";
            case "False Knight Arena" -> "Arene du Faux Chevalier";
            default -> englishName;
        };
    }

    private static String translateYesNo(String value) {
        if ("YES".equalsIgnoreCase(value)) return "OUI";
        if ("NO".equalsIgnoreCase(value)) return "NON";
        return value;
    }
}
