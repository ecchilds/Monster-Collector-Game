package com.mygdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.mygdx.game.entities.creatures.Creature;
import com.mygdx.game.entities.creatures.Player;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GameWorld {

    // player
    private static Player player;

    // rooms
    private static Room currentRoom;
    private static final Map<String, Room> adjacentRooms = new ConcurrentHashMap<>();
    private static OrthogonalTiledMapRenderer mapRenderer;

    // physics engine debug renderer
    private static final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    private GameWorld() {

    }

    public static void init(String initialRoom, SpriteBatch batch) {
        adjacentRooms.put("mansion2-v2", new Room("mansion2-v2")); //TODO: expand
        currentRoom = new Room(initialRoom);
        mapRenderer = new OrthogonalTiledMapRenderer(currentRoom.getMap(), 1/Config.PIXELS_PER_METER, batch); //TODO: try passing in spritebatch, bc apparently I can do that!

        player = spawnPlayer();
    }

    public static void update(float delta) {

    }

    public static void render(float delta) {
        currentRoom.render(mapRenderer, delta);
        update(delta);
    }

    public static void debugRender(OrthographicCamera camera, float delta) {
        render(delta);
        debugRenderer.render(currentRoom.getWorld(), camera.combined);
    }

    public static void setView(OrthographicCamera camera) {
        mapRenderer.setView(camera);
    }

    public static Player spawnPlayer() {
        return currentRoom.spawnNewCreature(Player::new, "enter");
    }

    //TODO: in multiplayer, playerManager will need to be used instead.
    // In preparation for that, this generally should not be called outside of GameScreen.
    // Instead, find a way to get a reference to that particular player.
    public static Player getPlayer() {
        return player;
    }

    public static Room getCurrentRoom() {
        return currentRoom;
    }

    public static Room getRoom(String mapName) {
        if(currentRoom.getMapName().equals(mapName)) {
            return currentRoom;
        } else {
            return adjacentRooms.get(mapName);
        }
    }

    public static void movePlayerToRoom(String mapName) {
        String spawnPointName = currentRoom.getMapName();

        // remove player from old room. this prevents leftover physics bodies from staying behind.
        // (under the hood, this only marks the player's body for safe deletion later, though it
        // does remove player from the render list)
        currentRoom.deSpawn(player);

        // put away old room
        currentRoom.unfocus();
        adjacentRooms.put(spawnPointName, currentRoom);

        // get new room
        currentRoom = adjacentRooms.get(mapName);
        adjacentRooms.remove(mapName);

        // prepare new room for rendering
        currentRoom.focus();
        mapRenderer.setMap(currentRoom.getMap());

        //spawn player into new room
        currentRoom.spawn(player, spawnPointName);
    }


    public static void moveCreatureToRoom(Creature creature, String sourceMap, String destinationMap) {
        Room source;
        Room destination;

        if (Objects.equals(sourceMap, currentRoom.getMapName())) {
            source = currentRoom;
            destination = adjacentRooms.get(destinationMap);
        } else if (Objects.equals(destinationMap, currentRoom.getMapName())) {
            source = adjacentRooms.get(sourceMap);
            destination = currentRoom;
        } else {
            source = adjacentRooms.get(sourceMap);
            destination = adjacentRooms.get(destinationMap);
        }

        source.deSpawn(creature);
        destination.spawn(creature, sourceMap);
    }

    public static void dispose() {
        debugRenderer.dispose();
        currentRoom.dispose();
        for(Room room : adjacentRooms.values()) {
            room.dispose();
        }
        adjacentRooms.clear();
        mapRenderer.dispose();
    }
}
