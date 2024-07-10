package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.BodyLocation;

public class WatchAction extends TimedAction implements ActionUsesTarget {

    private BodyLocation targetBody;
    private Entity target;

    public WatchAction(Mob owner) {
        this(owner, 7);
    }

    public WatchAction(Mob owner, float timeToLive) {
        super(owner, timeToLive);
    }

    @Override
    public void timedUpdate(float delta) {
        if (!target.isExisting()) {
            end(false);
        }

        Vector2 targetPosition = targetBody.getPosition();
        owner.setOrientation((float)Math.atan2(-(targetPosition.x - owner.getX()), targetPosition.y - owner.getY()));
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
