package com.mygdx.game.entities.creatures.ai.actions;

import com.mygdx.game.entities.Entity;

public interface ActionUsesTarget  {

    Entity getTarget();

    void setTarget(Entity target);
}
