package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.creatures.ai.state.State;

public class StateTransitionActionWrapper<S extends Enum<S> & State<?, S>> extends BehaviorTreeAction {

    private final BehaviorTreeAction wrapped;

    // Utilized for the state transition
    MobAI<?, S> ai;
    S state;
    int statePriority;

    public StateTransitionActionWrapper(Mob mob, MobAI<?, S> ai, S state, int statePriority, BehaviorTreeAction wrapped) {
        super(mob);
        this.wrapped = wrapped;
        this.ai = ai;
        this.state = state;
        this.statePriority = statePriority;
    }

    @Override
    public void update(float delta) {
        wrapped.update(delta);
        if (wrapped.isEnded()) {
            ai.changeState(state, statePriority);
            end(true);
        }
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

    public void setNextState(S state, int statePriority) {
        this.state = state;
        this.statePriority = statePriority;
    }

    public BehaviorTreeAction getWrapped() {
        return wrapped;
    }
}
