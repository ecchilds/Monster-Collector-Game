package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.RaycastObstacleAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.Box2dRaycastCollisionDetector;

public class WanderAction extends Action {

    protected final Wander<Vector2> wander;
    protected final RaycastObstacleAvoidance<Vector2> avoidance;
    protected final CentralRayWithWhiskersConfiguration<Vector2> rayCfg;
    //protected BlendedSteering<Vector2> steering;
    protected final PrioritySteering<Vector2> steering;

    protected float wanderRate;
    private float timeSinceLastPause;

    public WanderAction(Mob owner) {
        super(owner);

        wander = new Wander<>(owner);
        rayCfg = new CentralRayWithWhiskersConfiguration<>(owner, 2f, 0.75f, 30f);
        Box2dRaycastCollisionDetector detector = new Box2dRaycastCollisionDetector(owner.getBody().getWorld());
        avoidance = new RaycastObstacleAvoidance<>(owner, rayCfg, detector);
        avoidance.setDistanceFromBoundary(01f);
        steering = new PrioritySteering<>(owner);
        steering.add(avoidance);
        steering.add(wander);

        // this code is from:
        // https://github.com/piotr-j/ggj2016/blob/master/core/src/com/mygdx/game/entities/Walker.java
        wander.setFaceEnabled(true)
                .setAlignTolerance(.01f)
                .setDecelerationRadius(.5f)
                .setTimeToTarget(0.1f)
                .setWanderOffset(1f)
                .setWanderOrientation(MathUtils.random(360))
                .setWanderRadius(3f)
                .setWanderRate(MathUtils.PI2 * 2);

        setWanderRate(1);

        timeSinceLastPause = 0;
    }

    public void setWanderRate(float wanderRate) {
        this.wanderRate = wanderRate;
    }

    @Override
    public void update(float delta) {

        timeSinceLastPause += delta;
        if(timeSinceLastPause < 0) {

            // code taken from here:
            // https://github.com/piotr-j/ggj2016/blob/master/core/src/com/mygdx/game/entities/Walker.java
            steering.calculateSteering(steeringOutput);

            // Update position and linear velocity.
            if (!steeringOutput.linear.isZero()) {
                // this method internally scales the force by deltaTime
                owner.getBody().applyLinearImpulse(steeringOutput.linear.scl(owner.getBody().getMass()), owner.getBody().getWorldCenter(), true);
            }

            // Update orientation and angular velocity
            if (steeringOutput.angular != 0) {
                // this method internally scales the torque by deltaTime
                owner.getBody().applyTorque(steeringOutput.angular, true);
            }
        } else if(timeSinceLastPause > wanderRate) {
            timeSinceLastPause = -wanderRate;
        }

    }
}
