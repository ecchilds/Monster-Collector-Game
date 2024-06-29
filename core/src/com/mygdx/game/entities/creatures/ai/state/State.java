package com.mygdx.game.entities.creatures.ai.state;

import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.creatures.ai.actions.Action;

import java.util.List;

public interface State <T extends Mob, S extends Enum<S> & State<T, S>> {

    void enter(T mob, MobAI<T, S> ai, Action action, List<Entity> targets);

    // Whatever happens here can happens every time an action is created, even if that action is immediately discarded. Be careful!
    Action newInstance(T mob, MobAI<T, S> ai);

    void exit(T mob, Action action);

    default void handleMessage(Telegram telegram, T Mob, MobAI<T, S> ai) {
    }
}
