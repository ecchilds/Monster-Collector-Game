package com.mygdx.game.entities.creatures.ai.state;

import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.creatures.ai.actions.Action;
import com.mygdx.game.entities.creatures.ai.actions.ActionUsesTarget;

import java.util.List;

public interface State <T extends Mob, S extends Enum<S> & State<T, S>> {

    void enter(T mob, MobAI<T, S> ai, Action action, List<Entity> targets);

    // To be used ONLY for states that were the previous state. For example, if a monster pursuing an object of interest got
    // distracted by the player, it would still want to pursue that object once the player left. Therefore, the target should
    // remain unchanged between executions.
    // This returns an alternate state to be used, if this action is no longer viable for re-entry.
    // Do note that this will need to be overwritten for branch and wrapped actions, as they won't have a target.
    default S reEnter(T mob, MobAI<T, S> ai, Action action) {
        if (action instanceof ActionUsesTarget targetedAction) {
            if (targetedAction.getTarget().isExisting()) {
                enter(mob, ai, action, List.of(targetedAction.getTarget()));
            } else {
                return ai.getDefaultState();
            }
        } else {
            enter(mob, ai, action, List.of());
        }
        return null;
    }

    default boolean isReEnterable() {
        return true;
    }

    // Whatever happens here can happen every time an action is created, even if that action is immediately discarded. Be careful!
    Action newInstance(T mob, MobAI<T, S> ai);

    void exit(T mob, Action action);

    default boolean handleMessage(Telegram telegram, T Mob, MobAI<T, S> ai) {
        return false;
    }
}
