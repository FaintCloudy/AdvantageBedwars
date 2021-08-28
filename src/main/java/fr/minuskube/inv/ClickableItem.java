package fr.minuskube.inv;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface ClickableItem {

    ItemStack getItem();
    Consumer<InventoryClickEvent> consumer();



    static ClickableItem empty(ItemStack item) {
        return of(item, e -> {});
    }

    static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem() {

            @Override
            public Consumer<InventoryClickEvent> consumer() {
                return consumer;
            }

            @Override
            public ItemStack getItem() {
                return item;
            }
        };
    }

    default void run(InventoryClickEvent e) { consumer().accept(e); }


}
