package com.mygdx.game.entities.props;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Config;
import com.mygdx.game.Direction;
import com.mygdx.game.GameWorld;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.creatures.Mob;
import com.mygdx.game.entities.creatures.Player;

import java.util.ArrayList;
import java.util.List;

public class Door extends Prop {

    private final String destination;
    private final String source;
    private final List<Prop> subProps;

    public Door(RectangleMapObject door, World world, String sourceMap) {
        super(world, 0, 0);

        destination = door.getName();
        source = sourceMap;
        subProps = new ArrayList<>();

        PolygonShape shape = new PolygonShape();
        float x = door.getProperties().get("x", float.class) / Config.PIXELS_PER_METER;
        float y = door.getProperties().get("y", float.class) / Config.PIXELS_PER_METER;
        switch(door.getProperties().get("facing", String.class)) {
            case "down":
                this.facing = Direction.DOWN;
                shape.setAsBox(.4f, 1f / 16f);
                body.setTransform(x+0.5f, y+15/16f, 0);
                this.renderOffsetY = 1 + 15/16f;
                this.renderOffsetX = 1.5f;
                break;
            case "left":
                this.facing = Direction.LEFT;
                shape.setAsBox(1f / 16f, .4f);
                body.setTransform(
                        x+15/16f,
                        y+0.5f,
                        0);
                break;
            case "right":
                this.facing = Direction.RIGHT;
                shape.setAsBox(1f / 16f, .8f);
                body.setTransform(x+12/16f, y, 0);
                this.renderOffsetX = 12/16f;
                this.renderOffsetY = 1f;
                subProps.add(new Prop("prop-door.png", world, x+1+1/8f, y+17f/32f, 1/4f, 1/32f, 17/32f, 1/16f));
                subProps.add(new Prop("prop-doorverhang.png", world, x+1+1/8f, y-31/32f, 11/32f, 1/32f, 17/32f, 1/16f));
                break;
            case "up":
                this.facing = Direction.UP;
                shape.setAsBox(.4f, 1f / 16f);
                body.setTransform(
                        x+0.5f,
                        y+1/16f,
                        0);
                break;
            default:
                System.err.println("WARNING: invalid direction property on door.");
        }

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.dispose();

        this.addCollisionListener(Player.class, (Entity e) -> {
            GameWorld.movePlayerToRoom(destination);
        });
        this.addCollisionListener(Mob.class, (Entity e) -> {
            GameWorld.moveCreatureToRoom((Mob)e, source, destination);
        });

        this.putIdleSprite(Direction.DOWN, "prop-doorway.png");
        this.putIdleSprite(Direction.RIGHT, "prop-doorwayright.png");
    }

    public List<Prop> getSubProps() {
        return subProps;
    }

    //    @Override
//    public void draw(Batch batch, float delta) {
//        super.draw(batch, delta);
//        for (Prop prop : subProps) {
//            prop.draw(batch, delta);
//        }
//    }
}
