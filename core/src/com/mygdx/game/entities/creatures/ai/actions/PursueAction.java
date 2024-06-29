package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.BodyLocation;
import com.mygdx.game.entities.utils.EntityCollisionListener;

public class PursueAction extends BehaviorTreeAction implements ActionUsesTarget {

    Entity target;
    Seek<Vector2> seekBehavior = null;
    EntityCollisionListener collisionListener;

    public PursueAction(Mob owner, EntityCollisionListener collisionListener) {
        super(owner);
        this.collisionListener = collisionListener;
    }

    @Override
    public Entity getTarget() {
        return target;
    }

    @Override
    public void setTarget(Entity target) {
        this.target = target;
        if (seekBehavior == null) {
            seekBehavior = new Seek<>(owner, new BodyLocation(target.getBody()));
        } else {
            seekBehavior.setTarget(new BodyLocation(target.getBody()));
        }
        owner.addCollisionListener(target.getClass(), (targetInListener) -> {
            collisionListener.run(targetInListener);
            end();
        });
    }

    @Override
    public void update(float delta) {
        // code taken from here:
        // https://github.com/piotr-j/ggj2016/blob/master/core/src/com/mygdx/game/entities/Walker.java
        seekBehavior.calculateSteering(steeringOutput);

        // Update position and linear velocity.
        if (!steeringOutput.linear.isZero()) {
            // this method internally scales the force by deltaTime
            owner.getBody().applyLinearImpulse(steeringOutput.linear.scl(owner.getBody().getMass()), owner.getBody().getWorldCenter(), true);
        }

        // my code
        // change orientation so that correct vision cone orientations are maintained
        owner.setOrientation(owner.vectorToAngle(owner.getBody().getLinearVelocity()));
    }
}
