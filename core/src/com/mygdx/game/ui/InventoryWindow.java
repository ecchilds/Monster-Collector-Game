package com.mygdx.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.items.Inventory;

public class InventoryWindow extends WindowModal {

    Inventory inventory;
    Table inventoryList;
    ScrollPane scrollPane;

    public InventoryWindow(String title, Skin skin, String styleName, Inventory inventory) {
        super(title, skin, styleName);
        this.inventory = inventory;

        inventoryList = new InventoryTable(inventory, new Label.LabelStyle(getStyle().titleFont, getStyle().titleFontColor));

        Pixmap scrollBarBG = new Pixmap(10,1, Pixmap.Format.RGB565);
        scrollBarBG.setColor(Color.LIGHT_GRAY);
        scrollBarBG.fill();
        TextureRegionDrawable drawableScrollBarBG = new TextureRegionDrawable(new TextureRegion(new Texture(scrollBarBG)));

        Pixmap scrollBar = new Pixmap(10,1, Pixmap.Format.RGB565);
        scrollBar.setColor(Color.WHITE);
        scrollBar.fill();
        TextureRegionDrawable drawableScrollBar = new TextureRegionDrawable(new TextureRegion(new Texture(scrollBar)));

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = getStyle().background;
        scrollPaneStyle.vScroll = drawableScrollBarBG;
        scrollPaneStyle.vScrollKnob = drawableScrollBar;

        scrollPane = new ScrollPane(inventoryList, scrollPaneStyle);
        scrollPane.setScrollingDisabled(true, false);

        inventoryList.top().left();

        add(scrollPane).fill().expand();
    }
}
