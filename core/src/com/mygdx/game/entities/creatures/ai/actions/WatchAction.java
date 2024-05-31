package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.BodyLocation;
import com.mygdx.game.entities.creatures.ai.VoidFunction;

public class WatchAction extends LeafAction implements ActionUsesTarget<BodyLocation> {

    private float timeToLive;
    private float timeLived = 0;
    private BodyLocation target;

    public WatchAction(Mob owner, VoidFunction endedFunction) {
        this(owner, endedFunction, 7);
    }

    public WatchAction(Mob owner, VoidFunction endedFunction, float timeToLive) {
        super(owner, endedFunction);

        target = new BodyLocation(owner.getTarget().getBody());

        this.timeToLive = timeToLive;
    }

    @Override
    public void update(float delta) {
        timeLived += delta;

        if(timeLived < timeToLive) {
            Vector2 targetPosition = target.getPosition();
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
        target = new BodyLocation(owner.getTarget().getBody());
        timeLived = 0;
    }

    public void setTimeToLive(float timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public BodyLocation getTarget() {
        return target;
    }

    @Override
    public void setTarget(BodyLocation target) {
        this.target = target;
    }
}
