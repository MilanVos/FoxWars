package nl.phoenixdev.foxWars.fox;

import nl.phoenixdev.foxWars.game.Team;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;

import java.util.Objects;

public class FoxCore {
    private final Team team;
    private final Location spawnLocation;
    private Fox entity;
    private boolean alive;
    private double health = 20.0;

    public FoxCore(Team team, Location spawnLocation) {
        this.team = team;
        this.spawnLocation = spawnLocation;
        this.alive = true;
    }

    public void spawn() {
        if (entity != null) {
            entity.remove();
        }
        entity = (Fox) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.FOX);
        entity.setCustomName(team.getChatColor() + team.getName() + " Fox");
        entity.setCustomNameVisible(true);
        entity.setPersistent(true);
        entity.setRemoveWhenFarAway(false);
        
        // Give the Fox some "core" stats
        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(100.0);
        entity.setHealth(100.0);
    }

    public void handleDamage(Player attacker, double damage) {
        if (!alive) return;
        
        // Make the fox angry and attack back
        entity.setTarget(attacker);
        
        if (entity.getHealth() <= damage) {
            kill();
        }
    }

    private void kill() {
        this.alive = false;
        if (entity != null) {
            entity.remove();
        }
        // Notify game manager (handled later)
    }

    public Team getTeam() {
        return team;
    }

    public Fox getEntity() {
        return entity;
    }

    public boolean isAlive() {
        return alive;
    }
}
