package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;

public abstract class BehaviorTreeAction extends Action {

    protected boolean ended = false;

    public BehaviorTreeAction(Mob owner) {
        super(owner);
    }

    public boolean isEnded() {
        return ended;
    }

    public void end() {
        ended = true;
    }

    public void reset() {
        ended = false;
    }
}
