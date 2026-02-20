package nl.phoenixdev.foxWars.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ShopItem {
    BLOCKS(Material.ORANGE_WOOL, 16, Material.IRON_INGOT, 4, "Fox Fur (Blocks)"),
    SWORD(Material.STONE_SWORD, 1, Material.IRON_INGOT, 10, "Fox Tooth Sword"),
    ARMOR(Material.CHAINMAIL_CHESTPLATE, 1, Material.IRON_INGOT, 24, "Fox Leather Armor"),
    PEARL(Material.ENDER_PEARL, 1, Material.REDSTONE, 4, "Fox Blink"),
    GOLDEN_APPLE(Material.GOLDEN_APPLE, 1, Material.REDSTONE, 2, "Fox Berry");

    private final Material material;
    private final int amount;
    private final Material currency;
    private final int cost;
    private final String name;

    ShopItem(Material material, int amount, Material currency, int cost, String name) {
        this.material = material;
        this.amount = amount;
        this.currency = currency;
        this.cost = cost;
        this.name = name;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public Material getCurrency() {
        return currency;
    }

    public int getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }
}
