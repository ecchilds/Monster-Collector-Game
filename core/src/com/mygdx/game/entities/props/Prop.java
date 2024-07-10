package com.mygdx.game.entities.props;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Direction;
import com.mygdx.game.entities.AnimatedEntity;
import com.mygdx.game.entities.utils.FixtureBuilder;

public class Prop extends AnimatedEntity {

    public Prop(World world, float x, float y) {
        this(world, x, y, 0, 0);
    }

    public Prop(World world, float x, float y, float renderOffsetX, float renderOffsetY) {
        super(BodyDef.BodyType.StaticBody, world, x, y, renderOffsetX, renderOffsetY, 0, 0);
    }

    public Prop(String spriteName, World world, float x, float y, float renderOffsetX, float renderOffsetY, float collisionWidth, float collisionHeight) {
        super(BodyDef.BodyType.StaticBody, world, x, y, renderOffsetX, renderOffsetY, 0, 0);

        FixtureBuilder.buildSquare(body, collisionWidth, collisionHeight, 1f, 0.5f);
        putIdleSprite(Direction.DOWN, spriteName);
    }
}
