package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Config;
import com.mygdx.game.GameWorld;
import com.mygdx.game.PlayerManager;

public class GameScreen implements Screen {

    SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage ui;
    private Table mainUi;
    private Stack uiAndWindows;
    private Skin style;

    // meters on screen
    private static final float SCREEN_METERS = 25f;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_METERS, SCREEN_METERS * (Config.WINDOW_HEIGHT/(float)Config.WINDOW_WIDTH));

        // scene2d ui
        viewport = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        ui = new Stage(viewport, batch);
        mainUi = new Table();
        mainUi.setFillParent(true);
        uiAndWindows = new Stack(mainUi);
        uiAndWindows.setFillParent(true);
        ui.addActor(uiAndWindows);

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/teletactile2.fnt"), Gdx.files.internal("fonts/teletactile2.png"), false);
        style = new Skin();
        style.add("mainFont", font);
        Label label = new Label("GAPY", style, "mainFont", Color.WHITE);

        mainUi.add(label);
        mainUi.left().top();

        // physics engine
        Box2D.init();

        // rooms
        GameWorld.init("mansion-v2", batch);

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
        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
