package com.mygdx.game.entities.creatures.ai;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.actions.Action;
import com.mygdx.game.entities.creatures.ai.state.State;

import java.util.EnumMap;

public class MobAI<M extends Mob, S extends Enum<S> & State<M, S>> implements Telegraph {

    private S currentState;
    private S previousState = null;
    private final M owner;
    private final EnumMap<S, Action> actions;

    public MobAI(M owner, S initialState, Class<S> clazz) {
        this.owner = owner;
        currentState = initialState;

        actions = new EnumMap<>(clazz);
        Action newAction = initialState.newInstance(owner, this);
        actions.put(initialState, newAction);
        initialState.reEnter(owner, this, newAction);
    }

    public Action changeState(S newState) {

        if (newState == currentState) {
            return null;
        }

        currentState.exit(owner);
        previousState = currentState;
        currentState = newState;

        Action newAction = actions.get(newState);
        if (newAction == null) {
            newAction = currentState.newInstance(owner, this);
            actions.put(currentState, newAction);
            currentState.enter(owner);
        } else {
            currentState.reEnter(owner, this, newAction);
        }

        System.out.println("new State: " + newState.name() + " at time " + System.currentTimeMillis());

        return newAction;
    }

    public void revertState() {
        currentState.exit(owner);
        S tmpState = previousState;
        previousState = currentState;
        currentState = tmpState;
        currentState.reEnter(owner, this, actions.get(currentState));
    }

    public void update(float delta) {
        actions.get(currentState).update(delta);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        currentState.handleMessage(telegram, owner, this);
        return true;
    }
}
