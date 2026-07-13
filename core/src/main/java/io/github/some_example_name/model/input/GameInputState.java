package io.github.some_example_name.model.input;

public class GameInputState {
    private final boolean moveLeftHeld;
    private final boolean moveRightHeld;
    private final boolean lookUpHeld;
    private final boolean lookDownHeld;
    private final boolean jumpHeld;
    private final boolean jumpJustPressed;
    private final boolean dashJustPressed;
    private final boolean attackJustPressed;
    private final boolean spellJustPressed;
    private final boolean howlingWraithsJustPressed;
    private final boolean focusHeld;
    private final boolean interactJustPressed;
    private final boolean dialogueNextJustPressed;
    private final boolean pauseJustPressed;
    private final boolean inventoryJustPressed;

    public GameInputState(
        boolean moveLeftHeld,
        boolean moveRightHeld,
        boolean lookUpHeld,
        boolean lookDownHeld,
        boolean jumpHeld,
        boolean jumpJustPressed,
        boolean dashJustPressed,
        boolean attackJustPressed,
        boolean spellJustPressed,
        boolean howlingWraithsJustPressed,
        boolean focusHeld,
        boolean interactJustPressed,
        boolean dialogueNextJustPressed,
        boolean pauseJustPressed,
        boolean inventoryJustPressed
    ) {
        this.moveLeftHeld = moveLeftHeld;
        this.moveRightHeld = moveRightHeld;
        this.lookUpHeld = lookUpHeld;
        this.lookDownHeld = lookDownHeld;
        this.jumpHeld = jumpHeld;
        this.jumpJustPressed = jumpJustPressed;
        this.dashJustPressed = dashJustPressed;
        this.attackJustPressed = attackJustPressed;
        this.spellJustPressed = spellJustPressed;
        this.howlingWraithsJustPressed = howlingWraithsJustPressed;
        this.focusHeld = focusHeld;
        this.interactJustPressed = interactJustPressed;
        this.dialogueNextJustPressed = dialogueNextJustPressed;
        this.pauseJustPressed = pauseJustPressed;
        this.inventoryJustPressed = inventoryJustPressed;
    }

    public boolean isMoveLeftHeld() { return moveLeftHeld; }
    public boolean isMoveRightHeld() { return moveRightHeld; }
    public boolean isLookUpHeld() { return lookUpHeld; }
    public boolean isLookDownHeld() { return lookDownHeld; }
    public boolean isJumpHeld() { return jumpHeld; }
    public boolean isJumpJustPressed() { return jumpJustPressed; }
    public boolean isDashJustPressed() { return dashJustPressed; }
    public boolean isAttackJustPressed() { return attackJustPressed; }
    public boolean isSpellJustPressed() { return spellJustPressed; }
    public boolean isHowlingWraithsJustPressed() { return howlingWraithsJustPressed; }
    public boolean isFocusHeld() { return focusHeld; }
    public boolean isInteractJustPressed() { return interactJustPressed; }
    public boolean isDialogueNextJustPressed() { return dialogueNextJustPressed; }
    public boolean isPauseJustPressed() { return pauseJustPressed; }
    public boolean isInventoryJustPressed() { return inventoryJustPressed; }
}
