package nl.phoenixdev.foxWars.listener;

import nl.phoenixdev.foxWars.fox.FoxCore;
import nl.phoenixdev.foxWars.game.GameManager;
import nl.phoenixdev.foxWars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        // Find which team this fox belongs to
        for (Team team : Team.values()) {
            FoxCore core = gameManager.getFox(team);
            if (core != null && core.getEntity() != null && core.getEntity().equals(fox)) {
                // Check if it's the attacker's own fox
                if (gameManager.getTeam(attacker) == team) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "You cannot hit your own Fox!");
                    return;
                }
                
                core.handleDamage(attacker, event.getFinalDamage());
                
                if (!core.isAlive()) {
                    Bukkit.broadcastMessage(team.getChatColor() + team.getName() + " Fox has been killed!");
                }
                return;
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
            // Respawn logic (simplified)
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.spigot().respawn();
            }, 100L); // 5 seconds
        } else {
            player.sendMessage(ChatColor.RED + "Your Fox is dead! You will not respawn.");
            player.setAllowFlight(true); // Spectator mode (simple)
        }
    }
}
