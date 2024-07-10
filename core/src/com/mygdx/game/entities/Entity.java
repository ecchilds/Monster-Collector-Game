package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.entities.utils.EntityCollisionListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Entity {

    // variables for rendering
    protected float renderOffsetX;
    protected float renderOffsetY;
    protected float height;
    protected float width;

    protected Body body;
    protected final BodyDef.BodyType bodyType;
    protected float impulseSpeed;

    private boolean existing = false;

    private final Map<Class<?>, EntityCollisionListener> collisionListeners = new ConcurrentHashMap<>();

    public Entity(World world) {
        this(world, 0,0);
    }

    public Entity(World world, float x, float y) {
        this(BodyDef.BodyType.StaticBody, world, x, y);
    }

    public Entity(BodyDef.BodyType bodyType, World world, float x, float y) {
        this.bodyType = bodyType;
        impulseSpeed = 0f;

        this.generateBody(world, x, y);
    }

    public Entity(BodyDef.BodyType bodyType, World world,
                  float x, float y,
                  float renderOffsetX, float renderOffsetY,
                  float renderWidth, float renderHeight) {
        this(bodyType, world, x, y);

        this.width = renderWidth;
        this.height = renderHeight;

        this.renderOffsetX = renderOffsetX;
        this.renderOffsetY = renderOffsetY;
    }

    protected final void generateBody(World world, float x, float y) {

        // create physics body in given world, at given coordinates
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);
        this.body = world.createBody(bodyDef);
        this.body.setUserData(this);
    }

    public abstract void draw(Batch batch, float delta);

    public void addCollisionListener(Class<? extends Entity> clazz, EntityCollisionListener listener) {
        collisionListeners.put(clazz, listener);
    }

    public void handleCollision(Entity entity) {
        collisionListeners.entrySet().stream().filter((entry) -> entry.getKey().isAssignableFrom(entity.getClass())).forEach(entry -> entry.getValue().run(entity));
    }

    public void removeCollisionListener(Class<? extends Entity> clazz) {
        collisionListeners.remove(clazz);
    }

    public float getImpulseSpeed() {
        return impulseSpeed;
    }

    public void setImpulseSpeed(float newSpeed) {
        impulseSpeed = newSpeed;
    }

    public Body getBody() {
        return body;
    }

    public float getX() {
        return body.getPosition().x;
    }

    public float getY() {
        return body.getPosition().y;
    }

    public boolean isExisting() {
        return existing;
    }

    public void setExisting(boolean existing) {
        this.existing = existing;
    }

    public abstract void dispose();
}
