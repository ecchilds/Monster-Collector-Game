package com.mygdx.game.control;

import com.badlogic.gdx.InputProcessor;

public interface CharacterController extends InputProcessor {

    // Returns angle
    float getMovement();
}
