package nl.phoenixdev.foxWars.listener;

import nl.phoenixdev.foxWars.FoxWars;
import nl.phoenixdev.foxWars.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final FoxWars plugin;

    public JoinListener(FoxWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getGameManager().getState() == GameState.LOBBY) {
            Location lobby = plugin.getConfigManager().getLocation("lobby");
            if (lobby != null) {
                player.teleport(lobby);
            }
            player.sendMessage(ChatColor.YELLOW + "Welcome to FoxWars! Waiting for players...");
            
            checkStart();
        } else {
            player.sendMessage(ChatColor.RED + "Game already in progress!");
            player.setAllowFlight(true);
        }
    }

    private void checkStart() {
        int minPlayers = plugin.getConfig().getInt("min-players", 2);
        int countdown = plugin.getConfig().getInt("countdown-seconds", 10);
        
        if (plugin.getServer().getOnlinePlayers().size() >= minPlayers) { 
            plugin.getGameManager().setState(GameState.STARTING);
            plugin.getServer().broadcastMessage(ChatColor.GREEN + "Sufficient players joined! Game starting in " + countdown + " seconds...");
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (plugin.getServer().getOnlinePlayers().size() >= minPlayers) {
                    plugin.getGameManager().startGame(plugin);
                } else {
                    plugin.getGameManager().setState(GameState.LOBBY);
                    plugin.getServer().broadcastMessage(ChatColor.RED + "Not enough players! Countdown cancelled.");
                }
            }, countdown * 20L); 
        }
    }
}
