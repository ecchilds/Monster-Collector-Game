package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.creatures.Mob;

public abstract class TimedAction extends BehaviorTreeAction {

    private float timeToLive;
    private float timeLived = 0;

    public TimedAction(Mob owner, float timeToLive) {
        super(owner);
        this.timeToLive = timeToLive;
    }

    @Override
    public void update(float delta) {
        timeLived += delta;

        if(timeLived < timeToLive) {
            timedUpdate(delta);
        } else {
            end(true);
        }
    }

    abstract public void timedUpdate(float delta);

    /**
     * Reset "time lived" counter.
     */
    @Override
    public void resetAndSleep() {
        super.resetAndSleep();
        timeLived = 0;
    }

    public void setTimeToLive(float timeToLive) {
        this.timeToLive = timeToLive;
    }
}
