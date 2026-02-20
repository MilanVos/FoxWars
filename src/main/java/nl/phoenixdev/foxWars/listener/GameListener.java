package nl.phoenixdev.foxWars.listener;

import nl.phoenixdev.foxWars.FoxWars;
import nl.phoenixdev.foxWars.fox.FoxCore;
import nl.phoenixdev.foxWars.game.GameManager;
import nl.phoenixdev.foxWars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GameListener implements Listener {
    private final GameManager gameManager;
    private final JavaPlugin plugin;

    public GameListener(GameManager gameManager, JavaPlugin plugin) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoxDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Fox fox)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        for (Team team : Team.values()) {
            FoxCore core = gameManager.getFox(team);
            if (core != null && core.getEntity() != null && core.getEntity().equals(fox)) {
                if (gameManager.getTeam(attacker) == team) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "You cannot hit your own Fox!");
                    return;
                }
                
                core.handleDamage(attacker, event.getFinalDamage());
                
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fox.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
                }, 1L);
                
                if (!core.isAlive()) {
                    Bukkit.broadcastMessage(team.getChatColor() + team.getName() + " Fox has been killed!");
                    gameManager.checkWinner((FoxWars) plugin);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onVillagerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == org.bukkit.entity.EntityType.VILLAGER) {
            if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().contains("Fox Shop")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team team = gameManager.getTeam(player);
        
        if (team == null) return;

        if (gameManager.isFoxAlive(team)) {
            player.sendMessage(ChatColor.GREEN + "Your Fox is alive! You will respawn soon.");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                FoxWars foxWars = (FoxWars) plugin;
                Location spawnLoc = foxWars.getConfigManager().getLocation("teams." + team.name() + ".spawn");
                if (spawnLoc != null) {
                    player.spigot().respawn();
                    player.teleport(spawnLoc);
                } else {
                    player.spigot().respawn();
                }
            }, 100L);
        } else {

            player.sendMessage(ChatColor.RED + "Your Fox is dead! You will not respawn.");
            player.setAllowFlight(true);
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        }
        
        gameManager.checkWinner((FoxWars) plugin);
    }
}
