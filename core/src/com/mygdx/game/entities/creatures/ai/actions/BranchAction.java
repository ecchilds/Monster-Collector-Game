package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;

import java.util.List;

public abstract class BranchAction extends BehaviorTreeAction {

    protected final List<BehaviorTreeAction> actions;

    public BranchAction(Mob owner, BehaviorTreeAction... actions) {
        super(owner);
        this.actions = List.of(actions);
    }

    @Override
    public void start() {
        super.start();
        for (var action : actions) {
            action.start();
        }
    }

    @Override
    public void resetAndSleep() {
        super.resetAndSleep();
        for (var action : actions) {
            action.resetAndSleep();
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
