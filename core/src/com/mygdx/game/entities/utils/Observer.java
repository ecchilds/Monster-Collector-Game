package com.mygdx.game.entities.utils;

import com.mygdx.game.entities.Entity;

/**
 * Use this interface for entities with vision cones or hearing circles.
 */
public interface Observer {

    void handleObservation(Entity entity);

    void addObservationListener(Class<? extends Entity> clazz, EntityCollisionListener listener);

    void removeObservationListener(Class<? extends Entity> clazz);

}
