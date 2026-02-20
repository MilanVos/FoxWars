package nl.phoenixdev.foxWars.generator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Generator {
    public enum Type {
        SHARD(Material.IRON_INGOT, ChatColor.GRAY + "Shard", 40L), // 2 seconds
        BLOOD(Material.REDSTONE, ChatColor.RED + "Blood", 100L);   // 5 seconds

        private final Material material;
        private final String name;
        private final long delay;

        Type(Material material, String name, long delay) {
            this.material = material;
            this.name = name;
            this.delay = delay;
        }
    }

    private final Location location;
    private final Type type;
    private int taskId;

    public Generator(Location location, Type type) {
        this.location = location;
        this.type = type;
    }

    public void start(JavaPlugin plugin) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            ItemStack item = new ItemStack(type.material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(type.name);
                item.setItemMeta(meta);
            }
            location.getWorld().dropItem(location.clone().add(0, 1, 0), item);
        }, 0L, type.delay);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}
