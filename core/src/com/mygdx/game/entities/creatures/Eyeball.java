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
import com.mygdx.game.entities.creatures.ai.ObservationListener;
import com.mygdx.game.entities.creatures.ai.Observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Eyeball extends Mob implements Observer {

    Map<Class<?>, ObservationListener> observationListeners = new ConcurrentHashMap<>();
    MobAI<Eyeball, EyeballState> ai = new MobAI<>(this, EyeballState.WANDER, EyeballState.class);
    //VisionCone visionCone = new VisionCone(this, 2, MathUtils.PI/4);

    public Eyeball(World world, float x, float y) {
        super(world, x, y, 0.2f, 0.5f, 0.3f);

        // AI
        generateDetectionFixture();
        addObservationListener(Player.class, (Entity e) -> {
            this.setTarget(e);
            ai.changeState(EyeballState.WATCH);
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
        ObservationListener listener = observationListeners.get(entity.getClass());
        if(listener != null) {
            listener.run(entity);
        }
    }

    @Override
    public <T extends Entity> void addObservationListener(Class<T> clazz, ObservationListener listener) {
        observationListeners.put(clazz, listener);
    }

    @Override
    public <T extends Entity> void removeObservationListener(Class<T> clazz) {
        observationListeners.remove(clazz);
    }
}
