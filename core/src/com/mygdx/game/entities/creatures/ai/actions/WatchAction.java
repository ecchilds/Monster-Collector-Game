package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.BodyLocation;

public class WatchAction extends BehaviorTreeAction implements ActionUsesTarget {

    private float timeToLive;
    private float timeLived = 0;
    private BodyLocation targetBody;
    private Entity target;

    public WatchAction(Mob owner) {
        this(owner, 7);
    }

    public WatchAction(Mob owner, float timeToLive) {
        super(owner);
        this.timeToLive = timeToLive;
    }

    @Override
    public void update(float delta) {
        timeLived += delta;

        if(timeLived < timeToLive) {
            Vector2 targetPosition = targetBody.getPosition();
            owner.setOrientation((float)Math.atan2(-(targetPosition.x - owner.getX()), targetPosition.y - owner.getY()));
        } else {
            end();
        }
    }

    /**
     * Update target and reset "time lived" counter.
     */
    @Override
    public void reset() {
        super.reset();
        timeLived = 0;
    }

    public void setTimeToLive(float timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public Entity getTarget() {
        return target;
    }

    @Override
    public void setTarget(Entity target) {
        this.target = target;
        targetBody = new BodyLocation(target.getBody());
    }
}
