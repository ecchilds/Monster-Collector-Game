package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;

public class ActionSequence extends BranchAction {

    int index = 0;
    int numberOfActions;

    public ActionSequence(Mob owner, BehaviorTreeAction... actions) {
        super(owner, actions);
        numberOfActions = actions.length;
    }

    @Override
    public void update(float delta) {
        var action = actions.get(index);
        action.update(delta);
        if (action.isEnded()) {
            index++;
            if(index >= numberOfActions) {
                end();
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        index = 0;
    }
}
