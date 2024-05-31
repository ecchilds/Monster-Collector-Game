package com.mygdx.game.entities.creatures.ai.state;

import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.creatures.ai.actions.Action;

public interface State <T extends Mob, S extends Enum<S> & State<T, S>> {

    // action is needed in case data in the action needs to be changed, like the target for instance
    void enter(T mob);

    default void reEnter(T mob, MobAI<T, S> ai, Action action) {
        enter(mob);
    }

    Action newInstance(T mob, MobAI<T, S> ai);

    void exit(T mob);

    default void handleMessage(Telegram telegram, T Mob, MobAI<T, S> ai) {
    }
}
