package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class WindowGroup extends WidgetGroup {

    private final Skin skin;
    private final String styleName;

    private WindowModal testWindow = null;

    private final Map<Class<? extends WindowModal>, WindowModal> windows = new ConcurrentHashMap<>();

    public WindowGroup(Skin skin, String styleName) {
        this.skin = skin;
        this.styleName = styleName;
        setTouchable(Touchable.childrenOnly);
    }

    public void toggleTestWindow() {
        if (testWindow == null) {
            testWindow = new WindowModal("testwindow", skin, styleName);
            addActor(testWindow);
            testWindow.setX(getWidth()/2);
            testWindow.setY(getHeight()/2);
        } else {
            testWindow.toggleVisible();
            testWindow.toFront();
        }
    }

    @FunctionalInterface
    public interface WindowModalConstructor {
        WindowModal generate(String title, Skin skin, String styleName);
    }

    public <W extends WindowModal> void toggleWindow(Class<W> clazz, WindowModalConstructor constructor, String title) {
        WindowModal windowModal = windows.get(clazz);
        if (windowModal == null) {
            windowModal = constructor.generate(title, skin, styleName);
            addActor(windowModal);
            windows.put(clazz, windowModal);
            windowModal.setX(getWidth()/2 - windowModal.getWidth()/2);
            windowModal.setY(getHeight()/2 - windowModal.getHeight()/2);
        } else {
            windowModal.toggleVisible();
            windowModal.toFront();
        }
    }

    // Returns whether there were any visible windows
    public boolean toggleUpperMostWindow() {
        for (int i = getChildren().size-1; i >= 0; i--) {
            var actor = getChildren().items[i];
            if (actor.isVisible()) {
                actor.setVisible(false);
                return true;
            }
        }
        return false;
    }
}
