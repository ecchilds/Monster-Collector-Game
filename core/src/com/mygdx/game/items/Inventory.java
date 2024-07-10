package com.mygdx.game.items;

import java.util.Collection;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Inventory {

    // Sorted alphabetically by item name to ease inventory rendering.
    private final SortedMap<String, ItemStack> items = new ConcurrentSkipListMap<>();

    public Inventory() {
    }

    public Inventory(Collection<ItemStack> existingInventory) {
        for (ItemStack itemStack : existingInventory) {
            addItemStack(itemStack);
        }
    }

    public boolean addItem(Item item) {
        return items.computeIfAbsent(item.getName(), name -> new ItemStack(item, 0)).incrementCount(1) == 0;
    }

    public boolean removeItem(Item item) {
        ItemStack toRemoveFrom = items.get(item.getName());
        if (toRemoveFrom != null && toRemoveFrom.getCount() > 0) {
            toRemoveFrom.incrementCount(-1);

            if (toRemoveFrom.getCount() == 0) {
                items.remove(item.getName());
            }

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
}
