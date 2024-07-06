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
//    private Viewport viewport;
//    private Stage ui;
//    private Table mainUi;
//    private Stack uiAndWindows;
//    private Skin style;

    // meters on screen
    private static final float SCREEN_METERS = 25f;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_METERS, SCREEN_METERS * (Config.WINDOW_HEIGHT/(float)Config.WINDOW_WIDTH));

        // scene2d ui
        GameUI.init(batch);
//        viewport = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
//        ui = new Stage(viewport, batch);
//        mainUi = new Table();
//        mainUi.setFillParent(true);
//        uiAndWindows = new Stack(mainUi);
//        uiAndWindows.setFillParent(true);
//        ui.setRoot(uiAndWindows);
//
//        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/teletactile2.fnt"), Gdx.files.internal("fonts/teletactile2.png"), false);
//        style = new Skin();
//        style.add("mainFont", font);
//        Label label = new Label("GAPY", style, "mainFont", Color.WHITE);
//
//        //mainUi.add(label);
//        //mainUi.left().top();
//
//        // windows
//        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
//        bgPixmap.setColor(Color.DARK_GRAY);
//        bgPixmap.fill();
//        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
//
//        Window.WindowStyle windowStyle = new Window.WindowStyle();
//        windowStyle.titleFont = font;
//        windowStyle.titleFontColor = Color.WHITE;
//        windowStyle.background = textureRegionDrawableBg;
//        style.add("window", windowStyle, Window.WindowStyle.class);
//
//        WindowGroup windowStack = new WindowGroup(style, "window");
//        windowStack.setFillParent(true);
//        uiAndWindows.add(windowStack);

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
        //viewport.update(width, height);
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
