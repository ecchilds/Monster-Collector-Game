package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Config;
import com.mygdx.game.GameWorld;
import com.mygdx.game.entities.utils.FixtureBuilder;
import com.mygdx.game.items.Item;

public class ItemEntity extends Entity {

    private static final float DAMPING = 8f;
    protected final String roomName;
    protected final Item item;

    public ItemEntity(Item item, String roomName, World world, float x, float y, Vector2 initialVelocity) {
        this(item, roomName, world, x, y, 0.1f, 0.5f, 0.3f, initialVelocity);
    }

    public ItemEntity(Item item, String roomName, World world, float x, float y, float radius, float density, float friction) {
        super(BodyDef.BodyType.DynamicBody, world, x, y, 0.23f, 0.15f, 0, 0);
        this.roomName = roomName;
        this.item = item;

        item.initialize();

        // set width and height dynamically
        if(height == 0 || width == 0) {
            this.height = item.getSprite().getRegionHeight() / Config.PIXELS_PER_METER;
            this.width = item.getSprite().getRegionWidth() / Config.PIXELS_PER_METER;
        }

        FixtureBuilder.buildCircle(body, radius, density, friction);
        body.setLinearDamping(DAMPING);
    }

    public ItemEntity(Item item, String roomName, World world, float x, float y, float radius, float density, float friction, Vector2 initialVelocity) {
        this(item, roomName, world, x, y, radius, density, friction);
        body.setLinearVelocity(initialVelocity);
    }

    public void pickUp() {
        GameWorld.getRoom(roomName).deSpawn(this);
        dispose();
    }

    @Override
    public void draw(Batch batch, float delta) {
        Vector2 pos = body.getPosition();
        batch.draw(item.getSprite(), pos.x-renderOffsetX, pos.y-renderOffsetY, width, height);
    }

    @Override
    public void handleCollision(Entity entity) {
        super.handleCollision(entity);
    }

    @Override
    public void dispose() {
        item.dispose();
    }
}
