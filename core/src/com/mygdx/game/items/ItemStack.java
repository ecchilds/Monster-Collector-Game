package com.mygdx.game.items;

public class ItemStack {
    private final Item item;
    private int count;

    public ItemStack(Item item, int count) {
        this.item = item;
        this.count = count;

        item.initialize();
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    // Returns the remainder, if the entirety of delta cannot be consumed.
    public int incrementCount(int delta) {
        int newValue = count + delta;
        if (newValue > item.getMaxStackSize()) {
            int diff = item.getMaxStackSize() - newValue;
            count = item.getMaxStackSize();
            return diff;
        } else {
            count = newValue;
            return 0;
        }
    }

    public void dispose() {
        item.dispose();
    }
}
