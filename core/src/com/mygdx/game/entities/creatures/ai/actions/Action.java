package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;

public abstract class Action {

    protected Mob owner;

    // this is used by the update function. it is stored as a class variable to save on processing time.
    protected final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());;

    public Action(Mob owner) {
        this.owner = owner;
    }

    public Mob getOwner() {
        return owner;
    }

    abstract public void update(float delta);
}
