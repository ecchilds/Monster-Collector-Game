package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public enum Direction {
    LEFT,
    RIGHT,
    DOWN,
    UP;

    private static final float piAngle = MathUtils.PI / 4;

    public static Direction fromAngle(float angle) {

        angle = (angle + MathUtils.PI2 - piAngle) % MathUtils.PI2;

        if (angle < 2*piAngle) {
            return LEFT;
        } else if (angle < 4*piAngle) {
            return DOWN;
        } else if (angle < 6*piAngle) {
            return RIGHT;
        } else {
            return UP;
        }
    }

    public Vector2 toVector() {
        switch (this) {
            case UP:
                return new Vector2(0, 1);
            case DOWN:
                return new Vector2(0, -1);
            case RIGHT:
                return new Vector2(1, 0);
            case LEFT:
                return new Vector2(-1, 0);
            default:
                return null;
        }
    }
}
