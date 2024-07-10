package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.ActionStatus;
import com.mygdx.game.entities.creatures.ai.MobAI;

public class StateReversionActionWrapper extends ActionWrapper {

    final MobAI<?, ?> ai;
    final int statePriority;

    public StateReversionActionWrapper(Mob owner, BehaviorTreeAction wrapped, MobAI<?,?> ai, int statePriority) {
        super(owner, wrapped);
        this.ai = ai;
        this.statePriority = statePriority;
    }

    @Override
    public void update(float delta) {
        wrapped.update(delta);
        if (wrapped.isEnded()) {
            ai.revertState(statePriority);
            end(wrapped.getStatus() == ActionStatus.SUCCESS);
        }
    }
}
