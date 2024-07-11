package com.mygdx.game.items;

import java.util.Collection;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Inventory {

    // Sorted alphabetically by item name to ease inventory rendering.
    private final SortedMap<String, ItemStack> items = new ConcurrentSkipListMap<>();

    private boolean seenChange = false;

    public Inventory() {
    }

    public Inventory(Collection<ItemStack> existingInventory) {
        for (ItemStack itemStack : existingInventory) {
            addItemStack(itemStack);
        }
    }

    public boolean addItem(Item item) {
        if (items.computeIfAbsent(item.getName(), name -> new ItemStack(item, 0)).incrementCount(1) == 0) {
            seenChange = true;
            return true;
        }
        return false;
    }

    public boolean removeItem(Item item) {
        ItemStack toRemoveFrom = items.get(item.getName());
        if (toRemoveFrom != null && toRemoveFrom.getCount() > 0) {

            toRemoveFrom.incrementCount(-1);
            if (toRemoveFrom.getCount() == 0) {
                toRemoveFrom.dispose();
                items.remove(item.getName());
            }

            seenChange = true;

            return true;
        } else {
            return false;
        }
    }

    // Returns the amount left over in picked up stack.
    public int addItemStack(ItemStack itemStack) {
        ItemStack stored = items.get(itemStack.getItem().getName());
        if (stored == null) {
            items.put(itemStack.getItem().getName(), itemStack);
            return 0;
        } else {
            return stored.incrementCount(itemStack.getCount());
        }
    }

    public Iterable<ItemStack> getItems() {
        return items.values();
    }

    public boolean isSeenChange() {
        return seenChange;
    }

    public void setSeenChange(boolean seenChange) {
        this.seenChange = seenChange;
    }
}
