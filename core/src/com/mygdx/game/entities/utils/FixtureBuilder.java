package com.mygdx.game.entities.utils;

import com.badlogic.gdx.physics.box2d.*;

// helper functions to build and attach fixture to bodies
public class FixtureBuilder {

    public static void buildCircle(Body body, float radius, float density, float friction) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        buildAndAttach(body, shape, density, friction);
        shape.dispose();
    }

    public static void buildSquare(Body body, float width, float height, float density, float friction) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2f, height/2f);
        buildAndAttach(body, shape, density, friction);
        shape.dispose();
    }

    private static void buildAndAttach(Body body, Shape shape, float density, float friction) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        // leave restitution default
        body.createFixture(fixtureDef);
    }
}
