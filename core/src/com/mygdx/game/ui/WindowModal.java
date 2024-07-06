package com.mygdx.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

import java.util.List;

public class WindowModal extends Window {

    private TextButton closeButton;
    private static final List<Integer> MAPPED_KEYS = List.of(
            Input.Keys.UP,
            Input.Keys.DOWN,
            Input.Keys.ENTER
    );

    public WindowModal(String title, Skin skin, String styleName) {
        super(title, skin, styleName);

        TextButton.TextButtonStyle closeButtonStyle = new TextButton.TextButtonStyle();
        closeButtonStyle.font = skin.getFont("mainFont");
        closeButtonStyle.fontColor = Color.WHITE;

        closeButton = new TextButton("X", closeButtonStyle);
        closeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                toggleVisible();
                return true;
            }
        });
        getTitleTable().add(closeButton).width(16);
        padTop(14);

        Pixmap line = new Pixmap(1,2, Pixmap.Format.RGB565);
        line.setColor(new Color(0x0f0f0fff));
        line.fill();
        add(new Image(new TiledDrawable(new TextureRegion(new Texture(line))))).top().expandX().fillX().padTop(2);

        row();

        setTestText(new Label("Inventory", skin, "mainFont", Color.WHITE));

        setWidth(200);

        setModal(true);
        setMovable(true);
    }

    public void setTestText(Label label) {
        add(label).top().expand().fill();
    }

    public void toggleVisible() {
        setVisible(!isVisible());
        getStage().unfocus(this);
    }

    @Override
    public boolean notify(Event event, boolean capture) {
        if (event instanceof InputEvent inputEvent) {
            if ((inputEvent.getType() != InputEvent.Type.keyUp && inputEvent.getType() != InputEvent.Type.keyTyped && inputEvent.getType() != InputEvent.Type.keyDown) || MAPPED_KEYS.contains(inputEvent.getKeyCode())) {
                return super.notify(event, capture);
            } else {
                return event.isCancelled();
            }
        } else {
            return super.notify(event, capture);
        }
    }
}
