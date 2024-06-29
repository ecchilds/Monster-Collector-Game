package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.ActionStatus;

public abstract class BehaviorTreeAction extends Action {

    protected ActionStatus status = ActionStatus.SLEEPING;

    public BehaviorTreeAction(Mob owner) {
        super(owner);
    }

    public boolean isEnded() {
        return status != ActionStatus.RUNNING;
    }

    public void end(boolean success) {
        status = success ? ActionStatus.SUCCESS : ActionStatus.FAILED;
    }

    // Call this within the State's enter function.
    public void start() {
        status = ActionStatus.RUNNING;
    }

    // Call this within the State's exit function.
    public void resetAndSleep() {
        status = status == ActionStatus.RUNNING ? ActionStatus.INCOMPLETE : ActionStatus.SLEEPING;
    }

    public ActionStatus getStatus() {
        return status;
    }
}
