package com.mygdx.game;

import com.mygdx.game.entities.creatures.Player;

public class PlayerManager {

    private static Player player;

    private PlayerManager() {}

    public static void init() {
        player = GameWorld.spawnPlayer();
    };

    public static Player getCurrent() {
        return player;
    }
}
