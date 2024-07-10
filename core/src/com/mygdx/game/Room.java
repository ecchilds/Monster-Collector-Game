package com.mygdx.game;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.entities.*;
import com.mygdx.game.entities.creatures.Creature;
import com.mygdx.game.entities.creatures.Eyeball;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.utils.Observer;
import com.mygdx.game.entities.props.Door;
import com.mygdx.game.entities.utils.FixtureBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Room {

    private static class ListenerClass implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            Object entityA = contact.getFixtureA().getBody().getUserData();
            Object entityB = contact.getFixtureB().getBody().getUserData();
            if(entityB instanceof Entity && entityA instanceof Entity) {
                if(contact.getFixtureB().isSensor() && entityB instanceof Observer) {
                    ((Observer) entityB).handleObservation((Entity) entityA);
                    return;
                } else if(contact.getFixtureA().isSensor() && entityA instanceof Observer) {
                    ((Observer) entityA).handleObservation((Entity) entityB);
                    return;
                }
                ((Entity) entityA).handleCollision((Entity) entityB);
                ((Entity) entityB).handleCollision((Entity) entityA);
            }
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }

    private static final ContactListener listener = new ListenerClass();

    // map layers
    private static final int[] BACKGROUND_LAYERS = {0};
    private static final int COLLISION_LAYER = 1;
    private static final int SPAWNPOINT_LAYER = 2;
    private static final int DOOR_LAYER = 3;
    private static final int PROP_LAYER = 4;
    private static final int[] FOREGROUND_LAYERS = {5};

    // entity lists
    private final List<VisibleEntity> visibleEntities;
    private final List<Door> doors;

    // the "reason why this class exists" objects
    private final String mapName;
    private TiledMap map;
    private final World world;

    // used for safe deletion outside of physics steps
    private final List<Body> toBeDestroyed; //TODO: look into libgdx Array, its supposed to be better

    // this effectively keeps track of left over time from previous frames, which the physics engine didn't get to. it is to be simulated in a later frame
    private float accumulator = 0;

    public Room(String mapName) {
        this.mapName = mapName;
        map = new TmxMapLoader().load(mapName+".tmx");
        world = new World(new Vector2(0, 0), true);

        world.setContactListener(listener);
        toBeDestroyed = new LinkedList<>(); // TODO: change to LinkedBlockingDeque

        // initialize entity lists
        visibleEntities = new ArrayList<>();
        doors = new ArrayList<>();

        // Monsters
        for(EllipseMapObject spawnPoint : map.getLayers().get(SPAWNPOINT_LAYER).getObjects().getByType(EllipseMapObject.class)) { // TODO: consider not using circular spawnpoints as "monster only"
            switch (spawnPoint.getName()) {
                case "eyeball":
                    visibleEntities.add(new Eyeball(world,
                            spawnPoint.getProperties().get("x", float.class) / Config.PIXELS_PER_METER + 0.5f,
                            spawnPoint.getProperties().get("y", float.class) / Config.PIXELS_PER_METER + 0.5f));
            }
        }

        // Collision Boxes
        for(RectangleMapObject mapRectangle : map.getLayers().get(COLLISION_LAYER).getObjects().getByType(RectangleMapObject.class)) {

            Rectangle rectangle = mapRectangle.getRectangle();

            // these modifications are necessary because in Box2d, the height and width are measured
            // as a distance from the center, not a measurement of the sides
            float w = rectangle.getWidth()/Config.PIXELS_PER_METER;
            float h = rectangle.getHeight()/Config.PIXELS_PER_METER;

            // initialize body at co-ords given
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rectangle.x/Config.PIXELS_PER_METER + w/2f, rectangle.y/Config.PIXELS_PER_METER + h/2f); //Box2d sets position at center, but world co-ords are bottom-left corner
            Body body = world.createBody(bodyDef);

            // define dimensions and physics properties
            FixtureBuilder.buildSquare(body, w, h, 0.5f, 0.5f);
        }

        // doors
        for(RectangleMapObject mapRectangle : map.getLayers().get(DOOR_LAYER).getObjects().getByType(RectangleMapObject.class)) {
            Door door = new Door(mapRectangle, world, mapName);
            doors.add(door);
            visibleEntities.addAll(door.getSubProps());
        }
    }

    public World getWorld() {
        return world;
    }

    public TiledMap getMap() {
        return map;
    }

    public String getMapName() {
        return mapName;
    }

    @FunctionalInterface
    public interface CreatureConstructor<T, R> {
        R apply(T a1, float a2, float a3);
    }

    public <T extends Creature> T spawnNewCreature(CreatureConstructor<World, T> constructor, String spawnPointName) {

        // retrieve spawn point
        MapProperties props = map.getLayers().get(SPAWNPOINT_LAYER).getObjects().get(spawnPointName).getProperties();

        // spawn creature
        return spawnNewCreature(constructor, props.get("x", float.class), props.get("y", float.class));
    }

    public <T extends Creature> T spawnNewCreature(CreatureConstructor<World, T> constructor, float x, float y) {
        T creature = constructor.apply(world, x / Config.PIXELS_PER_METER + 0.5f, y / Config.PIXELS_PER_METER + 0.5f);
        visibleEntities.add(creature);
        creature.setExisting(true);
        return creature;
    }

    @FunctionalInterface
    public interface ItemConstructor<R> {
        R build(String roomName, World world, float x, float y, Vector2 initialVelocity);
    }

    public <T extends Item> T spawnNewItem(ItemConstructor<T> constructor, float x, float y, Vector2 initialVelocity) {
        T item = constructor.build(mapName, world, x, y, initialVelocity);
        visibleEntities.add(item);
        item.setExisting(true);
        return item;
    }

    /**
     *  WARNING: do NOT call this function without first de-spawning the creature from
     *   the previous room! Not doing so will cause copious physics problems.
     * @param creature Creature to be spawned in. This generates a new physics body for it.
     * @param spawnPointName The name of the target spawn point.
     * @param <T> The type of creature. This can be anything that extends the Creature class.
     */
    public <T extends Creature> void spawn(T creature, String spawnPointName) {
        MapProperties props = map.getLayers().get(SPAWNPOINT_LAYER).getObjects().get(spawnPointName).getProperties();
        creature.moveToRoom(this, props.get("x", float.class) / Config.PIXELS_PER_METER + .5f, props.get("y", float.class) / Config.PIXELS_PER_METER + .5f);
        visibleEntities.add(creature);
        creature.setExisting(true);
    }

    public void deSpawn(VisibleEntity entity) {
        if(visibleEntities.contains(entity)) {
            visibleEntities.remove(entity);
            entity.setExisting(false);
            toBeDestroyed.add(entity.getBody());
        }
    }

    public void render(OrthogonalTiledMapRenderer mapRenderer, float delta) {
        Batch mapBatch = mapRenderer.getBatch();

        // render
        mapRenderer.render(BACKGROUND_LAYERS);
        mapBatch.begin();
        for(Door door : doors) {
            door.draw(mapBatch, delta);
        }
        visibleEntities.stream()
                .sorted((VisibleEntity e1, VisibleEntity e2) -> e1.getY()-e2.getY() > 0 ? -1 : 1)
                .forEachOrdered((VisibleEntity e) -> e.draw(mapBatch, delta));
        mapBatch.end();
        mapRenderer.render(FOREGROUND_LAYERS);

        // update monster AI's
        GdxAI.getTimepiece().update(delta);
        visibleEntities.stream().
                filter((VisibleEntity e) -> e instanceof Mob).
                forEach((VisibleEntity e) -> {
                    ((Mob)e).update(delta);
                });

        // safe delete bodies before physics step
        for(Body body : toBeDestroyed) {
            world.destroyBody(body);
        }
        toBeDestroyed.clear();

        // physics step(s)
        // -----------------
        // add the time for the current frame, up to a maximum of a quarter second so as not to slow down old computers too much
        accumulator += Math.min(delta, 0.25f);

        // simulate physics steps until there is not enough time left in the frame for any more steps
        while(accumulator >= Config.PHYSICS_TIME_STEP) {

            //simulate physics step
            world.step(Config.PHYSICS_TIME_STEP, Config.PHYSICS_VELOCITY_ITERATIONS, Config.PHYSICS_POSITION_ITERATIONS);
            accumulator -= Config.PHYSICS_TIME_STEP;
        }
    }

    public void unfocus() {
        map.dispose();
    }

    public void focus() {
        map = new TmxMapLoader().load(mapName+".tmx");
    }

    public void dispose() {
        world.dispose();
        map.dispose();
        doors.forEach(VisibleEntity::dispose);
        visibleEntities.forEach(VisibleEntity::dispose);
    }
}
