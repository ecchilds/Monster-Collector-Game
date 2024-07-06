package com.mygdx.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Config;

public class GameUI {

    private static Viewport viewport;
    private static Stage stage;
    private static Table overlay;
    private static Stack overlayAndWindows;
    private static WindowGroup windows;
    private static Skin style;

    public static void init(SpriteBatch batch) {
        viewport = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        stage = new Stage(viewport, batch);
        overlay = new Table();
        overlay.setFillParent(true);
        overlayAndWindows = new Stack(overlay);
        overlayAndWindows.setFillParent(true);
        stage.setRoot(overlayAndWindows);

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/teletactile2.fnt"), Gdx.files.internal("fonts/teletactile2.png"), false);
        style = new Skin();
        style.add("mainFont", font);

        Label label = new Label("Press I to open inventory", style, "mainFont", Color.WHITE);
        overlay.add(label).left().top();
        overlay.top().left();

        // windows
        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.DARK_GRAY);
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = textureRegionDrawableBg;
        style.add("window", windowStyle, Window.WindowStyle.class);

        windows = new WindowGroup(style, "window");
        windows.setFillParent(true);
        overlayAndWindows.add(windows);
    }

    public static void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public static Stage getStage() {
        return stage;
    }

    public static WindowGroup getWindows() {
        return windows;
    }
}
