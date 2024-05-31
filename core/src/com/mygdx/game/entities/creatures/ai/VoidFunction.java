package com.mygdx.game.entities.creatures.ai;

@FunctionalInterface
public interface VoidFunction {

    void apply();

    default VoidFunction afterApply(VoidFunction andThen) {
        return () -> {
            this.apply();
            andThen.apply();
        };
    }

    default VoidFunction beforeApply(VoidFunction beforeEnd) {
        return () -> {
            beforeEnd.apply();
            this.apply();
        };
    }
}
