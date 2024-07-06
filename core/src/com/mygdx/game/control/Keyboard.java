package com.mygdx.game.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.entities.creatures.Player;
import com.mygdx.game.ui.GameUI;

public class Keyboard implements CharacterController {

    private final Player player;

    public Keyboard(Player player) {
        this.player = player;
    }

    @Override
    public float getMovement() {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            return MathUtils.PI;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            return 0;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            return MathUtils.HALF_PI;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            return MathUtils.HALF_PI * 3;
        } else {
            return -1;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.I) {
            GameUI.toggleTestWindow();
            return true;
        } else {
            return player.handleKeyRelease(keycode);
        }
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
