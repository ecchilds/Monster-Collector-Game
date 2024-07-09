package com.mygdx.game.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.entities.utils.EntityCollisionListener;

import java.util.HashMap;
import java.util.Optional;

public abstract class Entity {

    protected Body body;
    protected BodyDef.BodyType bodyType;
    protected float impulseSpeed;

    HashMap<Class<?>, EntityCollisionListener> collisionListeners;

    public Entity(World world) {
        this(world, 0,0);
    }

    public Entity(World world, float x, float y) {
        this(BodyDef.BodyType.StaticBody, world, x, y);
    }

    public Entity(BodyDef.BodyType bodyType, World world, float x, float y) {
        this.bodyType = bodyType;
        impulseSpeed = 0f;

        collisionListeners = new HashMap<>();

        this.generateBody(world, x, y);
    }

    protected final void generateBody(World world, float x, float y) {

        // create physics body in given world, at given coordinates
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);
        this.body = world.createBody(bodyDef);
        this.body.setUserData(this);
    }

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
}
