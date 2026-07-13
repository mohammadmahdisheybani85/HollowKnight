package io.github.some_example_name.view.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.some_example_name.controller.input.KeyBindingController;
import io.github.some_example_name.model.input.GameInputState;
import io.github.some_example_name.model.input.PlayerAction;

public class GameInputReader {
    private final KeyBindingController keyBindingController;

    public GameInputReader(KeyBindingController keyBindingController) {
        this.keyBindingController = keyBindingController;
    }

    public GameInputState read() {
        boolean lookUpHeld =
            isPressed(PlayerAction.LOOK_UP)
                || Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.UP);

        boolean lookDownHeld =
            isPressed(PlayerAction.LOOK_DOWN)
                || Gdx.input.isKeyPressed(Input.Keys.S)
                || Gdx.input.isKeyPressed(Input.Keys.DOWN);

        boolean attackJustPressed =
            isJustPressed(PlayerAction.ATTACK)
                || Gdx.input.isKeyJustPressed(Input.Keys.J);

        boolean spellJustPressed =
            isJustPressed(PlayerAction.CAST_SPELL)
                || Gdx.input.isKeyJustPressed(Input.Keys.K);

        boolean jumpHeld =
            isPressed(PlayerAction.JUMP)
                || Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyPressed(Input.Keys.Z);

        boolean jumpJustPressed =
            isJustPressed(PlayerAction.JUMP)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.Z);

        boolean dashJustPressed =
            isJustPressed(PlayerAction.DASH)
                || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)
                || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)
                || Gdx.input.isKeyJustPressed(Input.Keys.C);

        return new GameInputState(
            isPressed(PlayerAction.MOVE_LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.LEFT),
            isPressed(PlayerAction.MOVE_RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.D)
                || Gdx.input.isKeyPressed(Input.Keys.RIGHT),
            lookUpHeld,
            lookDownHeld,
            jumpHeld,
            jumpJustPressed,
            dashJustPressed,
            attackJustPressed,
            spellJustPressed && !lookUpHeld,
            spellJustPressed && lookUpHeld,
            isPressed(PlayerAction.FOCUS)
                || Gdx.input.isKeyPressed(Input.Keys.F),
            isJustPressed(PlayerAction.INTERACT)
                || Gdx.input.isKeyJustPressed(Input.Keys.E),
            Gdx.input.isKeyJustPressed(Input.Keys.ENTER),
            isJustPressed(PlayerAction.PAUSE)
                || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE),
            isJustPressed(PlayerAction.OPEN_INVENTORY)
                || Gdx.input.isKeyJustPressed(Input.Keys.I)
        );
    }

    private boolean isPressed(PlayerAction action) {
        return Gdx.input.isKeyPressed(
            keyBindingController.getKeyCode(action)
        );
    }

    private boolean isJustPressed(PlayerAction action) {
        return Gdx.input.isKeyJustPressed(
            keyBindingController.getKeyCode(action)
        );
    }
}
