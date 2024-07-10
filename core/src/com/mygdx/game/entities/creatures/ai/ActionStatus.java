package com.mygdx.game.entities.creatures.ai;

public enum ActionStatus {
    RUNNING,
    FAILED,
    SUCCESS,
    SLEEPING, // Used when the action is not in use, and completed in success or failure.
    INCOMPLETE // Used when the action is not in use, but not did not finish. Only occurs on interrupt
}
