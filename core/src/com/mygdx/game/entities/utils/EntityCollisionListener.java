package com.mygdx.game.entities.utils;

import com.mygdx.game.entities.Entity;

@FunctionalInterface
public interface EntityCollisionListener {
    void run(Entity source);
}
