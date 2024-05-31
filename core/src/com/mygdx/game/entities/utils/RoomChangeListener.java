package com.mygdx.game.entities.utils;

import com.mygdx.game.Room;
import com.mygdx.game.entities.Entity;

//TODO: implement these bad boys into Creature, so that mobs can follow players who change rooms.

@FunctionalInterface
public interface RoomChangeListener {

    void run(Entity watchingForChange, Room room);

}
