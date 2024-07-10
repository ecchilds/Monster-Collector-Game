package com.mygdx.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {
    private final String spriteName;
    private final String name;
    private final int id;
    private final int maxStackSize = 10; // TODO: move this number to a constant, and add it to a constructor parameter

    private TextureRegion sprite = null;
    private int currentUsers = 0;

    public Item(String spriteName, String name, int id) {
        this.spriteName = spriteName;
        this.name = name;
        this.id = id;
    }

    public TextureRegion getSprite() {
        return sprite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void initialize() {
        if (currentUsers == 0) {
            sprite = new TextureRegion(new Texture(Gdx.files.internal(spriteName)));
        }
        currentUsers++;
    }

    public void dispose() {
        currentUsers--;
        if (currentUsers == 0) {
            sprite.getTexture().dispose();
            sprite = null;
        }
    }
}
