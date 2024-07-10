package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Config;
import com.mygdx.game.Direction;

import java.util.EnumMap;
import java.util.Map;

abstract public class AnimatedEntity extends Entity {

    protected final Map<Direction, TextureRegion> idleSprites = new EnumMap<>(Direction.class);
    protected Direction facing = Direction.DOWN;

    public AnimatedEntity(BodyDef.BodyType bodyType, World world) {
        this(bodyType, world, 0, 0);
    }

    public AnimatedEntity(BodyDef.BodyType bodyType, World world, float x, float y) {
        this(bodyType, world, x, y, 0, 0, 0, 0);
    }

    public AnimatedEntity(BodyDef.BodyType bodyType, World world,
                          float x, float y,
                          float renderOffsetX, float renderOffsetY,
                          float renderWidth, float renderHeight) {
        super(bodyType, world, x, y, renderOffsetX, renderOffsetY, renderWidth, renderHeight);
    }

    //NOTE: delta is currently unused by VisibleEntity, but will be necessary when idle animations are implemented.
    public void draw(Batch batch, float delta) {
        Vector2 pos = body.getPosition();
        batch.draw(getIdleSprite(), pos.x-renderOffsetX, pos.y-renderOffsetY, width, height);
    }

    public void putIdleSprite(Direction facing, String filename) {
        TextureRegion textureRegion = new TextureRegion(new Texture(Gdx.files.internal(filename)));

        // set width and height dynamically
        if(height == 0 || width == 0) {
            this.height = textureRegion.getRegionHeight() / Config.PIXELS_PER_METER;
            this.width = textureRegion.getRegionWidth() / Config.PIXELS_PER_METER;
        }

        // store sprite
        idleSprites.put(facing, textureRegion);

        // flip sprite if possible
        if(facing == Direction.LEFT && idleSprites.get(Direction.RIGHT) == null) {
            TextureRegion right = new TextureRegion(idleSprites.get(Direction.LEFT));
            right.flip(true, false);
            idleSprites.put(Direction.RIGHT, right);
        }
    }

    public TextureRegion getIdleSprite() {
        return idleSprites.get(facing);
    }

    @Override
    public void dispose() {
        for (TextureRegion sprite : idleSprites.values()) {
            sprite.getTexture().dispose();
        }
        idleSprites.clear();
    }
}
