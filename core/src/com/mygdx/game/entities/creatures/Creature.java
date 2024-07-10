package com.mygdx.game.entities.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Direction;
import com.mygdx.game.Room;
import com.mygdx.game.entities.AnimatedEntity;
import com.mygdx.game.entities.utils.FixtureBuilder;
import com.mygdx.game.entities.utils.MessageTypes;

import java.util.*;

//TODO: more robust textures and animations. move ALL animation rules to "VisibleEntity"

abstract public class Creature extends AnimatedEntity implements Telegraph {

    // Physics values. Stored for when creature moves between rooms.
    private final float radius;
    private final float density;
    private final float friction;

    // This is here for debugging purposes. Ignore the warning.
    private final String name;

    // Variables for use in animations
    protected float animationTime;
    protected final Map<Direction, Animation<TextureRegion>> walkingAnimations = new EnumMap<>(Direction.class);

    protected final Array<Telegraph> watchers = new Array<>(); // TODO: consider a different collection from java library

    public Creature(World world, float x, float y) {
        this("john smith", world, x, y);
    }

    public Creature(String name, World world, float x, float y) {
        this(name, world, x, y, 0.5f, 1f, 0.5f);
    }

    public Creature(String name, World world, float x, float y, float radius, float density, float friction) {
        this(name, world, x, y, radius, density, friction,
                0.5f, 0.2f,
                1f, 1f);
    }

    public Creature(String name, World world,
                    float x, float y,
                    float radius, float density, float friction,
                    float renderOffsetX, float renderOffsetY,
                    float renderWidth, float renderHeight) {
        super(BodyDef.BodyType.DynamicBody, world, x, y, renderOffsetX, renderOffsetY, renderWidth, renderHeight);

        this.name = name;
        this.radius = radius;
        this.density = density;
        this.friction = friction;

        FixtureBuilder.buildCircle(body, radius, density, friction);
        body.setLinearDamping(getDefaultDamping());
    }

    // NOTE: these next three functions feel EXTREMELY illegal, especially getDefaultDamping, since it is
    // called both in the constructor and in moveToRoom, but is expected to Overridden by child classes.
    // Ask Megan if this is good coding practice.

    /**
     * This returns 1f in the parent class, but is expected to be overridden.
     * For reference, Player uses 10f instead.
     * @return The default damping value, used on each initialization.
     */
    protected float getDefaultDamping() {
        return 1f;
    }

    /**
     *  Use this function to spawn entities into new rooms.
     *
     *  WARNING: do NOT call this function without first de-spawning the creature from
     *   the previous room! Not doing so will cause copious physics problems.
     * @param room The room to be moved to.
     * @param x The creature's starting X coordinate in the new world.
     * @param y The creature's starting Y coordinate in the new world.
     */
    public void moveToRoom(Room room, float x, float y) {

        // make new body
        generateBody(room.getWorld(), x, y);
        FixtureBuilder.buildCircle(body, radius, density, friction);
        body.setLinearDamping(getDefaultDamping());

        // ping all listeners
        for (Telegraph watcher : watchers) {
            MessageManager.getInstance().dispatchMessage(this, watcher, MessageTypes.ROOM_CHANGE, room);
        }
    }

    public void putWalkingAnimation(Direction facing, String filename, int cols, int rows) {
        Texture animationSheet = new Texture(Gdx.files.internal(filename));

        TextureRegion[][] tmp = TextureRegion.split(animationSheet,
                animationSheet.getWidth() / cols,
                animationSheet.getHeight() / rows);

        TextureRegion[] animationFrames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                animationFrames[index++] = tmp[i][j];
            }
        }


        if(facing == Direction.LEFT && walkingAnimations.get(Direction.RIGHT) == null) {

            // generate a right-walking animation if one does not already exist
            TextureRegion[] right = new TextureRegion[animationFrames.length];
            for(int i = 0; i < animationFrames.length; i++) {
                right[i] = new TextureRegion(animationFrames[i]);
                right[i].flip(true, false);
            }
            walkingAnimations.put(Direction.RIGHT, new Animation<>(0.25f, right));
        }

        walkingAnimations.put(facing, new Animation<>(0.25f, animationFrames));

        //TODO: try disposing textures and such
    }

    public TextureRegion getCurrentSprite(float timeElapsed) {

        // determine direction
        Vector2 velocity = this.body.getLinearVelocity();
        float xMagnitude = Math.abs(velocity.x);
        float yMagnitude = Math.abs(velocity.y);
        if (xMagnitude > yMagnitude && xMagnitude > 0.5) {
            if(velocity.x > 0) {
                facing = Direction.RIGHT;
            } else if (velocity.x < 0) {
                facing = Direction.LEFT;
            }
        } else if (yMagnitude > 0.5) {
            if (velocity.y > 0) {
                facing = Direction.UP;
            } else if (velocity.y < 0) {
                facing = Direction.DOWN;
            }
        } else { // get idle sprite if the player is not moving
            return getIdleSprite();
        }

        // get animation if they are
        animationTime += timeElapsed;
        return walkingAnimations.get(facing).getKeyFrame(animationTime, true);
    }

    @Override
    public void draw(Batch batch, float delta) {
        Vector2 pos = body.getPosition();
        batch.draw(getCurrentSprite(delta), pos.x-renderOffsetX, pos.y-renderOffsetY, width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Animation animation : walkingAnimations.values()) {
            Arrays.stream(animation.getKeyFrames()).forEach((sprite) -> { ((TextureRegion) sprite).getTexture().dispose();});
        }
        walkingAnimations.clear();
    }

    // Telegraph Stuff
    // TODO: consider moving to Entity.
    // ----------------------------------------------------------------------------------------------------------------

    @Override
    abstract public boolean handleMessage(Telegram telegram);

    public void addWatcher(Telegraph watcher) {
        watchers.add(watcher);
    }

    public void removeWatcher(Telegraph watcher) {
        watchers.removeValue(watcher, false);
    }
}
