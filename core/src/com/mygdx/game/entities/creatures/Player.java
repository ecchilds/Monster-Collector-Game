package com.mygdx.game.entities.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Direction;
import com.mygdx.game.GameWorld;
import com.mygdx.game.control.CharacterController;
import com.mygdx.game.control.Keyboard;
import com.mygdx.game.entities.Apple;
import com.mygdx.game.entities.utils.MessageTypes;

import java.util.Objects;

public class Player extends Creature {

    private CharacterController controller;

    public Player(World world, float x, float y) {
        super("hoodie", world, x, y, 0.25f, 1f, 0.5f);

        controller = new Keyboard(this);
        Gdx.input.setInputProcessor(controller);

        body.setLinearDamping(getDefaultDamping());
        setImpulseSpeed(0.15f);

        // Initialize all animations
        putIdleSprite(Direction.DOWN, "hoodie-idle-down.png");
        putIdleSprite(Direction.UP, "hoodie-idle-up.png");
        putIdleSprite(Direction.LEFT, "hoodie-idle-left.png");
        this.putWalkingAnimation(Direction.UP, "hoodie-walk-up.png", 1, 1);
        this.putWalkingAnimation(Direction.DOWN, "hoodie-walk-down.png", 1, 1);
        this.putWalkingAnimation(Direction.LEFT, "hoodie-walk-left.png", 1, 1);
    }

    @Override
    protected float getDefaultDamping() {
        return 10f;
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        return true;
    }

    // =================================================================================================================
    // Input Handling
    // =================================================================================================================

    public void handleHeldKeys() {
        float angle = controller.getMovement();
        if (angle >= 0) {
            Vector2 playerPos = body.getPosition();
            body.applyLinearImpulse(new Vector2(getImpulseSpeed(), 0).setAngleRad(angle), playerPos, true);
        }
    }

    @Deprecated
    public boolean move(Direction direction) {
        Vector2 playerPos = body.getPosition();
        switch (direction) {
            case LEFT: {
                body.applyLinearImpulse(-getImpulseSpeed(), 0, playerPos.x, playerPos.y, true);
                break;
            } case RIGHT: {
                body.applyLinearImpulse(getImpulseSpeed(), 0, playerPos.x, playerPos.y, true);
                break;
            } case UP: {
                body.applyLinearImpulse(0, getImpulseSpeed(), playerPos.x, playerPos.y, true);
                break;
            } case DOWN: {
                body.applyLinearImpulse(0, -getImpulseSpeed(), playerPos.x, playerPos.y, true);
                break;
            } default: {
                return false;
            }
        }
        return true;
    }

    public boolean handleKeyRelease(int keyCode) {
        if (keyCode == Input.Keys.P) {
            Vector2 playerPos = body.getPosition();
            Vector2 facingVector = Objects.requireNonNull(facing.toVector());
            Apple apple = GameWorld.getCurrentRoom().spawnNewItem(Apple::new,
                    playerPos.x + facingVector.x / 2,
                    playerPos.y + facingVector.y / 2,
                    facingVector);
            for (Telegraph watcher : watchers) {
                MessageManager.getInstance().dispatchMessage(this, watcher, MessageTypes.ITEM_DROPPED, apple);
            }
            return true;
        }
        return false;
    }

//    @Override
//    public void draw(Batch batch, float delta) {
//        Vector2 pos = body.getPosition();
//        batch.draw(getCurrentSprite(delta), pos.x - 0.5f, pos.y - 0.2f, 1f, 1f);
//    }
}
