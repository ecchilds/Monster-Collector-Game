package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.ActionStatus;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.creatures.ai.state.State;

public class StateTransitionActionWrapper<S extends Enum<S> & State<?, S>> extends ActionWrapper {


    // Utilized for the state transition
    final MobAI<?, S> ai;
    S state;
    int statePriority;

    public StateTransitionActionWrapper(Mob mob, MobAI<?, S> ai, S state, int statePriority, BehaviorTreeAction wrapped) {
        super(mob, wrapped);
        this.ai = ai;
        this.state = state;
        this.statePriority = statePriority;
    }

    @Override
    public void update(float delta) {
        wrapped.update(delta);
        if (wrapped.isEnded()) {
            ai.changeState(state, statePriority);
            end(wrapped.getStatus() == ActionStatus.SUCCESS);
        }
    }

    public void setNextState(S state, int statePriority) {
        this.state = state;
        this.statePriority = statePriority;
    }
}
