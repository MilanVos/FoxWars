package nl.phoenixdev.foxWars.shop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopListener implements Listener {
    private static final String SHOP_TITLE = ChatColor.GOLD + "Fox Shop";

    @EventHandler
    public void onShopOpen(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.VILLAGER) return;
        Villager villager = (Villager) event.getRightClicked();
        if (!villager.getName().contains("Fox Shop")) return;

        event.setCancelled(true);
        openShop(event.getPlayer());
    }

    private void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, SHOP_TITLE);

        for (ShopItem item : ShopItem.values()) {
            ItemStack display = item.getItem();
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + item.getCost() + " " + (item.getCurrency() == Material.IRON_INGOT ? "Shards" : "Blood"));
                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            inv.addItem(display);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(SHOP_TITLE)) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        for (ShopItem shopItem : ShopItem.values()) {
            if (shopItem.getItem().getType() == event.getCurrentItem().getType()) {
                handlePurchase(player, shopItem);
                return;
            }
        }
    }

    private void handlePurchase(Player player, ShopItem shopItem) {
        if (player.getInventory().contains(shopItem.getCurrency(), shopItem.getCost())) {
            ItemStack payment = new ItemStack(shopItem.getCurrency(), shopItem.getCost());
            player.getInventory().removeItem(payment);
            player.getInventory().addItem(shopItem.getItem());
            player.sendMessage(ChatColor.GREEN + "Purchased " + shopItem.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have enough resources!");
        }
    }
}
