package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.entities.creatures.Mob;

public class ConfusionAction extends TimedAction {

    private static final float CONFUSION_INTERVAL = 0.3f;
    private static final float MAX_TURN_SPEED = .2f;
    private float timeSinceLastTurn = -CONFUSION_INTERVAL;
    private float currentTurnSpeed = nextSpeed();

    public ConfusionAction(Mob owner, float timeToLive) {
        super(owner, timeToLive);
    }

    @Override
    public void timedUpdate(float delta) {
        timeSinceLastTurn += delta;

        if (timeSinceLastTurn < 0) {
            owner.getBody().applyTorque(currentTurnSpeed, true);
        } else if (timeSinceLastTurn > CONFUSION_INTERVAL*3){
            timeSinceLastTurn = -CONFUSION_INTERVAL;
            currentTurnSpeed = nextSpeed();
        }
    }

    @Override
    public void resetAndSleep() {
        super.resetAndSleep();
        timeSinceLastTurn = -CONFUSION_INTERVAL;
        currentTurnSpeed = nextSpeed();
    }

    private static float nextSpeed() {
        return MAX_TURN_SPEED * MathUtils.randomSign();
    }
}
