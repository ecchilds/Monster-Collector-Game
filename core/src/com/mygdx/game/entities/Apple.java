package com.mygdx.game.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Apple extends Item{
    public Apple(String roomName, World world, float x, float y) {
        super("apple.png", roomName, world, x, y, 0.1f, 0.5f, 0.3f);
    }

    public Apple(String roomName, World world, float x, float y, Vector2 initialVelocity) {
        super("apple.png", roomName, world, x, y, 0.1f, 0.5f, 0.3f, initialVelocity);
    }
}
