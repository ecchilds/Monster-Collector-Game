package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Config;
import com.mygdx.game.GameWorld;
import com.mygdx.game.PlayerManager;
import com.mygdx.game.ui.GameUI;
import com.mygdx.game.ui.WindowGroup;
import com.mygdx.game.ui.WindowModal;

public class GameScreen implements Screen {

    SpriteBatch batch;
    private OrthographicCamera camera;

    // meters on screen
    private static final float SCREEN_METERS = 25f;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_METERS, SCREEN_METERS * (Config.WINDOW_HEIGHT/(float)Config.WINDOW_WIDTH));

        // scene2d ui
        GameUI.init(batch);


        // physics engine
        Box2D.init();

        // rooms
        GameWorld.init("mansion-v2", batch);

        // input
        InputMultiplexer multiplexer = new InputMultiplexer(GameUI.getStage(), GameWorld.getPlayer().getController());
        Gdx.input.setInputProcessor(multiplexer);

        // players
        //PlayerManager.init();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        // get player input
        GameWorld.getPlayer().handleHeldKeys();

        camera.position.set(GameWorld.getPlayer().getX(), GameWorld.getPlayer().getY(), 0);
        camera.position.x = Math.round(GameWorld.getPlayer().getX() * 64f) / 64f; // this is a hack that prevents texture bleeding. consider adding margins to tileset instead
        camera.update();

        // render
        GameWorld.setView(camera);
        GameWorld.debugRender(camera, delta);
        GameUI.render(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        GameWorld.dispose();
        PlayerManager.getCurrent().dispose();
    }
}
