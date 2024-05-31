package com.mygdx.game.entities.creatures.ai.actions;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.creatures.Mob;

public abstract class Action {

    protected Mob owner;

    // this is used by the update function. it is stored as a class variable to save on processing time.
    //NOTE: this could potentially be static to save memory, but since multiple creatures will likely be operating
    // concurrently at some point, I believe this would be a terrible idea.
    protected final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());;

    public Action(Mob owner) {
        this.owner = owner;
    }

    public Mob getOwner() {
        return owner;
    }

    abstract public void update(float delta);
}
