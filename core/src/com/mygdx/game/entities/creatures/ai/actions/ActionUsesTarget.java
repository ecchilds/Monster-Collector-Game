package com.mygdx.game.entities.creatures.ai.actions;

public interface ActionUsesTarget<T>  {

    T getTarget();

    void setTarget(T target);
}
