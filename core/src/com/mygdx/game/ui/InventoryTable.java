package com.mygdx.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.items.Inventory;
import com.mygdx.game.items.ItemStack;

public class InventoryTable extends Table {

    private final Inventory inventory;
    private final Label.LabelStyle labelStyle;


    public InventoryTable(Inventory inventory, Label.LabelStyle labelStyle) {
        this.inventory = inventory;
        this.labelStyle = labelStyle;
        layoutItems();
    }

    @Override
    public void layout() {
        super.layout();
        if (inventory.isSeenChange()) {
            if (getParent() instanceof ScrollPane scrollPane) {
                scrollPane.invalidate();
            }
            clear();
            layoutItems();
            inventory.setSeenChange(false);
        }
    }

    private void layoutItems() {
        for (ItemStack stack : inventory.getItems()) {
            add(new Label(stack.getItem().getName() + " (" + stack.getCount() + ")", labelStyle)).left().expandX().pad(5);
            add(new Image(stack.getItem().getSprite())).padRight(5).height(32).width(32);
            row();
        }
    }
}
