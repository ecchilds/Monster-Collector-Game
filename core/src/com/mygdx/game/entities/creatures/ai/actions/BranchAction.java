package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;

import java.util.List;

public abstract class BranchAction extends BehaviorTreeAction {

    protected List<BehaviorTreeAction> actions;

    public BranchAction(Mob owner, BehaviorTreeAction... actions) {
        super(owner);
        this.actions = List.of(actions);
    }

    @Override
    public void reset() {
        ended = false;
        for (var action : actions) {
            action.reset();
        }
    }

    public void setTargets(List<Entity> targets) {
        int targetIndex = 0;
        for (var action : actions) {
            if (action instanceof ActionUsesTarget) {
                ((ActionUsesTarget)action).setTarget(targets.get(targetIndex));
                targetIndex++;
            }
        }
    }
}
