package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class WindowGroup extends WidgetGroup {

    private final Skin skin;
    private final String styleName;

    private WindowModal testWindow = null;

    public WindowGroup(Skin skin, String styleName) {
        this.skin = skin;
        this.styleName = styleName;
        setTouchable(Touchable.childrenOnly);
    }

    public void toggleTestWindow() {
        if (testWindow == null) {
            testWindow = new WindowModal("testwindow", skin, styleName);
            addActor(testWindow);
            //testWindow.setX(getWidth()/2);
            //testWindow.setY(getHeight()/2);
        } else {
            testWindow.toggleVisible();
            testWindow.toFront();
        }
    }
}
