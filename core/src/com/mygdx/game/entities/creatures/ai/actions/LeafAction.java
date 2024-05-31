package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.VoidFunction;

public abstract class LeafAction extends Action {


    protected VoidFunction endedFunction;
    protected boolean ended = false;

    public LeafAction(Mob owner, VoidFunction endedFunction) {
        super(owner);
        this.endedFunction = endedFunction;
    }

    public boolean isEnded() {
        return ended;
    }

    public void end() {
        endedFunction.apply();
        ended = true;
    }

    public void reset() {
        ended = false;
    }

    public void appendToEndedFunction(VoidFunction appendedFunction) {
        endedFunction = endedFunction.afterApply(appendedFunction);
    }

    public void prependToEndedFunction(VoidFunction prependFunction) {
        endedFunction = endedFunction.beforeApply(prependFunction);
    }
}
