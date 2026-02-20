package nl.phoenixdev.foxWars.config;

import nl.phoenixdev.foxWars.FoxWars;
import nl.phoenixdev.foxWars.game.Team;
import nl.phoenixdev.foxWars.generator.Generator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final FoxWars plugin;
    private final FileConfiguration config;

    public ConfigManager(FoxWars plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }

    public void setLocation(String path, Location loc) {
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        plugin.saveConfig();
    }

    public Location getLocation(String path) {
        if (!config.contains(path)) return null;
        return new Location(
            Bukkit.getWorld(config.getString(path + ".world")),
            config.getDouble(path + ".x"),
            config.getDouble(path + ".y"),
            config.getDouble(path + ".z"),
            (float) config.getDouble(path + ".yaw"),
            (float) config.getDouble(path + ".pitch")
        );
    }

    public void addGeneratorLocation(Generator.Type type, Location loc) {
        List<String> gens = config.getStringList("generators." + type.name());
        gens.add(serializeLoc(loc));
        config.set("generators." + type.name(), gens);
        plugin.saveConfig();
    }

    public List<Location> getGeneratorLocations(Generator.Type type) {
        List<Location> locs = new ArrayList<>();
        for (String s : config.getStringList("generators." + type.name())) {
            locs.add(deserializeLoc(s));
        }
        return locs;
    }

    private String serializeLoc(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    private Location deserializeLoc(String s) {
        String[] parts = s.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}
