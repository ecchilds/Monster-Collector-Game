package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.ActionStatus;

public class ActionSequence extends BranchAction {

    int index = 0;
    final int numberOfActions;

    public ActionSequence(Mob owner, BehaviorTreeAction... actions) {
        super(owner, actions);
        numberOfActions = actions.length;
    }

    @Override
    public void update(float delta) {
        var action = actions.get(index);

        if (action.getStatus() == ActionStatus.FAILED) {
            end(false);
        } else if (action.isEnded()) {
            index++;
            if (index >= numberOfActions) {
                end(true);
            }
        } else {
            action.update(delta);
        }
    }

    @Override
    public void resetAndSleep() {
        super.resetAndSleep();
        index = 0;
    }
}
