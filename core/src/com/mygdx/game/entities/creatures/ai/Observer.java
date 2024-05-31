package com.mygdx.game.entities.creatures.ai;

import com.mygdx.game.entities.Entity;

/**
 * Use this interface for entities with vision cones or hearing circles.
 */
public interface Observer {

    void handleObservation(Entity entity);

    <T extends Entity> void addObservationListener(Class<T> clazz, ObservationListener listener);

    <T extends Entity> void removeObservationListener(Class<T> clazz);

}
