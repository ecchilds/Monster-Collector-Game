package com.mygdx.game.entities.creatures;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Direction;
import com.mygdx.game.Room;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.ai.state.EyeballState;
import com.mygdx.game.entities.creatures.ai.MobAI;
import com.mygdx.game.entities.utils.Observer;
import com.mygdx.game.entities.utils.EntityCollisionListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Eyeball extends Mob implements Observer {

    final Map<Class<? extends Entity>, EntityCollisionListener> observationListeners = new ConcurrentHashMap<>();
    final MobAI<Eyeball, EyeballState> ai = new MobAI<>(this, EyeballState.WANDER, EyeballState.class);

    public Eyeball(World world, float x, float y) {
        super(world, x, y, 0.2f, 0.5f, 0.3f);

        // AI
        generateDetectionFixture();
        addObservationListener(Player.class, (Entity e) -> {
            ai.changeState(EyeballState.WATCH_PLAYER, 4, List.of(e));
        });

        setMaxLinearSpeed(.5f);
        setMaxLinearAcceleration(.2f);
        setMaxAngularSpeed(3f);
        setMaxAngularAcceleration(.5f);
        setBoundingRadius(0.2f);

        putIdleSprite(Direction.DOWN, "eyeball-idle-down.png");
        putIdleSprite(Direction.UP, "eyeball-idle-up.png");
        putIdleSprite(Direction.LEFT, "eyeball-idle-left.png");
        putWalkingAnimation(Direction.DOWN, "eyeball-idle-down.png", 1,1);
        putWalkingAnimation(Direction.UP, "eyeball-idle-up.png", 1,1);
        putWalkingAnimation(Direction.LEFT, "eyeball-idle-left.png", 1,1);
    }

    @Override
    protected float getDefaultDamping() {
        return 5f;
    }

    @Override
    protected float getDefaultAngularDamping() {
        return 10f;
    }

    private void generateDetectionFixture() {
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(2);
        shape.setPosition(new Vector2(0, 1.9f));
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void moveToRoom(Room room, float x, float y) {
        super.moveToRoom(room, x, y);
        generateDetectionFixture();
    }

    @Override
    public void update(float delta) {
        ai.update(delta);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        return ai.handleMessage(telegram);
    }

    // Observer Functions
    // --------------------------------------------------------------------------------------------
    @Override
    public void handleObservation(Entity entity) {
        observationListeners.entrySet().stream().filter((entry) -> entry.getKey().isAssignableFrom(entity.getClass())).forEach(entry -> entry.getValue().run(entity));
    }

    @Override
    public void addObservationListener(Class<? extends Entity> clazz, EntityCollisionListener listener) {
        observationListeners.put(clazz, listener);
    }

    @Override
    public void removeObservationListener(Class<? extends Entity> clazz) {
        observationListeners.remove(clazz);
    }
}
