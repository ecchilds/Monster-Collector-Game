package com.mygdx.game.entities.creatures.ai;

import com.mygdx.game.entities.Entity;

@FunctionalInterface
public interface ObservationListener {
    void run(Entity entity);
}
