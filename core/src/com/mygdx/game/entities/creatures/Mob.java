package com.mygdx.game.entities.creatures;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Direction;
import com.mygdx.game.Room;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.ai.BodyLocation;

abstract public class Mob extends Creature implements Steerable<Vector2> {

    // Steerable variables
    private float maxLinearSpeed;
    private float maxLinearAcceleration;
    private float maxAngularSpeed;
    private float maxAngularAcceleration;
    private float zeroThreshold = 0.001f;
    private boolean tagged;
    private float boundingRadius;

    // used by some steering behaviors
    private Entity target;

    public Mob(World world, float x, float y) {
        super("mob", world, x, y);
        init();
    }

    public Mob(World world, float x, float y, float radius, float density, float friction) {
        super("mob", world, x, y, radius, density, friction);
        init();
    }

    private void init() {
        body.setAngularDamping(getDefaultAngularDamping());
        target = null;
    }

    @Override
    public void moveToRoom(Room room, float x, float y) {
        super.moveToRoom(room, x, y);
        body.setAngularDamping(getDefaultAngularDamping());
    }

    @Override
    public TextureRegion getCurrentSprite(float timeElapsed) {

        this.facing = Direction.fromAngle(this.getBody().getAngle());

        // get idle sprite if unmoving
        if(getLinearVelocity().len2() < 0.25) {
            return getIdleSprite();
        }

        // get animation if they are
        animationTime += timeElapsed;
        return walkingAnimations.get(facing).getKeyFrame(animationTime, true);
    }

    abstract public void update(float delta);

    /**
     * This returns 5f in the parent class, but is expected to be overridden.
     * For reference, Eyeball uses 10f instead.
     * @return The default angular damping value, used on each initialization.
     */
    protected float getDefaultAngularDamping() {
        return 5f;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    // Steerable stuff. Majority of code taken from:
    // https://github.com/piotr-j/ggj2016/blob/master/core/src/com/mygdx/game/utils/BodySteerable.java
    // ----------------------------------------------------------------------------------------------------------------
    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    public void setBoundingRadius(float boundingRadius) {
        this.boundingRadius = boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {
        // this is set blank in other BodySteerable. There's probably a good reason why.
        body.setTransform(getX(), getY(), orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector2> newLocation() {
        return new BodyLocation();
    }
}
