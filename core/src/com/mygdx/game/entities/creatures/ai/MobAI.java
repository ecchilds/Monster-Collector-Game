package com.mygdx.game.entities.creatures.ai;

import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.ai.actions.Action;
import com.mygdx.game.entities.creatures.ai.actions.BehaviorTreeAction;
import com.mygdx.game.entities.creatures.ai.state.State;

import java.util.EnumMap;
import java.util.List;

public class MobAI<M extends Mob, S extends Enum<S> & State<M, S>> {

    private S currentState;
    private S previousState = null;
    private boolean stateReversionOccurred = false; // Set to true if previous state is intentionally set, so that a special state re-entry transition can occur.
    private S defaultState;
    private S nextState = null;
    private List<Entity> nextStateTargets = List.of();

    // Used in case multiple state transitions occur in-between ai updates, to ensure only the most important one sticks.
    // In general:
    // 5 - caused by something scary to the mob
    // 4 - caused by the player
    // 3 - caused by something of interest to the mob
    // 2 - caused by a physics interaction
    // 1 - background state shuffling, such as returning to a default state after a StateTransitionAction ends
    // 0 - default, unused for state transitions
    private int nextStatePriority = 0;

    private final M owner;
    private final EnumMap<S, Action> actions;

    public MobAI(M owner, S initialState, Class<S> clazz) {
        this.owner = owner;
        currentState = initialState;
        defaultState = initialState;

        actions = new EnumMap<>(clazz);
        Action newAction = initialState.newInstance(owner, this);
        actions.put(initialState, newAction);
        initialState.enter(owner, this, newAction, List.of());
    }

    private boolean changeStateInternal(S newState, int priority, List<Entity> targets) {
        if (nextState == null || priority > nextStatePriority) {
            nextState = newState;
            nextStatePriority = priority;
            nextStateTargets = targets;
            stateReversionOccurred = false;
            return true;
        }
        return false;
    }

    public void changeState(S newState, int priority) {
        changeStateInternal(newState, priority, List.of());
    }

    public void changeState(S newState, int priority, List<Entity> targets) {
        changeStateInternal(newState, priority, targets);
    }

    public <A extends Action> A changeStateAndPreloadAction(S newState, int priority, Class<A> clazz) {
        return changeStateAndPreloadAction(newState, priority, List.of(), clazz);
    }

    public <A extends Action> A changeStateAndPreloadAction(S newState, int priority, List<Entity> targets, Class<A> clazz) {
        if (changeStateInternal(newState, priority, targets)) {
            return clazz.cast(actions.computeIfAbsent(newState, state -> state.newInstance(owner, this)));
        }
        return null;
    }

    public void revertState(int priority) {
        var previousAction = actions.get(previousState);
        if (previousState.isReEnterable() && previousState != currentState && (!(previousAction instanceof BehaviorTreeAction) || ((BehaviorTreeAction) previousAction).getStatus() == ActionStatus.INCOMPLETE)) {
            if (changeStateInternal(previousState, priority, List.of())) {
                stateReversionOccurred = true;
            }
        } else {
            changeStateInternal(defaultState, priority, List.of());
        }
    }

    public void update(float delta) {
        if (nextState != null) {
            currentState.exit(owner, actions.get(currentState));

            Action newAction = actions.computeIfAbsent(nextState, state -> state.newInstance(owner, this));
            if (nextState == previousState && stateReversionOccurred) {
                nextState.reEnter(owner, this, newAction);
                stateReversionOccurred = false;
            } else {
                nextState.enter(owner, this, newAction, nextStateTargets);
            }

            previousState = currentState;
            currentState = nextState;

            nextState = null;
            nextStatePriority = 0;
            nextStateTargets = List.of();

            newAction.update(delta);
        } else {
            actions.get(currentState).update(delta); // Note: this can change nextState. No way around that, as that's how state transitions are performed.
        }
    }

    public boolean handleMessage(Telegram telegram) {
        currentState.handleMessage(telegram, owner, this);
        return true;
    }

    public S getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(S defaultState) {
        this.defaultState = defaultState;
    }
}
