package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;

public abstract class ActionWrapper extends BehaviorTreeAction {

    protected final BehaviorTreeAction wrapped;

    public ActionWrapper(Mob owner, BehaviorTreeAction wrapped) {
        super(owner);
        this.wrapped = wrapped;
    }

    @Override
    public void start() {
        super.start();
        wrapped.start();
    }

    @Override
    public void resetAndSleep() {
        super.resetAndSleep();
        wrapped.resetAndSleep();
    }

    public BehaviorTreeAction getWrapped() {
        return wrapped;
    }
}
