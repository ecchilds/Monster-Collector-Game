package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Direction;
import com.mygdx.game.GameWorld;
import com.mygdx.game.entities.utils.FixtureBuilder;

abstract public class Item extends VisibleEntity {

    private static final float DAMPING = 8f;
    protected final String roomName;

    public Item(String spriteName, String roomName, World world, float x, float y, float radius, float density, float friction) {
        super(BodyDef.BodyType.DynamicBody, world, x, y, 0.23f, 0.15f, 0, 0);
        this.roomName = roomName;

        FixtureBuilder.buildCircle(body, radius, density, friction);
        body.setLinearDamping(DAMPING);

        putIdleSprite(Direction.DOWN, spriteName);
    }

    public Item(String spriteName, String roomName, World world, float x, float y, float radius, float density, float friction, Vector2 initialVelocity) {
        this(spriteName, roomName, world, x, y, radius, density, friction);
        body.setLinearVelocity(initialVelocity);
    }

    public void pickUp() {
        GameWorld.getRoom(roomName).deSpawn(this);
    }

    @Override
    public TextureRegion getIdleSprite() {
        return idleSprites.get(Direction.DOWN);
    }

    @Override
    public void handleCollision(Entity entity) {
        super.handleCollision(entity);
    }
}
