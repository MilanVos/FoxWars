package nl.phoenixdev.foxWars;

import nl.phoenixdev.foxWars.command.FoxWarsCommand;
import nl.phoenixdev.foxWars.command.FoxWarsTabCompleter;
import nl.phoenixdev.foxWars.config.ConfigManager;
import nl.phoenixdev.foxWars.game.GameManager;
import nl.phoenixdev.foxWars.game.Team;
import nl.phoenixdev.foxWars.listener.GameListener;
import nl.phoenixdev.foxWars.listener.JoinListener;
import nl.phoenixdev.foxWars.scoreboard.ScoreboardService;
import nl.phoenixdev.foxWars.shop.ShopListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoxWars extends JavaPlugin {
    private GameManager gameManager;
    private ConfigManager configManager;
    private ScoreboardService scoreboardService;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.gameManager = new GameManager();
        this.scoreboardService = new ScoreboardService(gameManager);
        
        getServer().getPluginManager().registerEvents(new GameListener(gameManager, this), this);
        getServer().getPluginManager().registerEvents(new ShopListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getCommand("fox").setExecutor(new FoxWarsCommand(this));
        getCommand("fox").setTabCompleter(new FoxWarsTabCompleter());

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(scoreboardService::updateScoreboard);
            
            gameManager.getFoxes().forEach(fox -> {
                fox.stayInPlace();
                
                if (fox.getEntity() != null) {
                    fox.getEntity().getNearbyEntities(5, 5, 5).forEach(entity -> {
                        if (entity instanceof Player player) {
                            Team playerTeam = gameManager.getTeam(player);
                            if (playerTeam != null && playerTeam != fox.getTeam()) {
                                fox.getEntity().setTarget(player);
                                
                                if (fox.getEntity().getLocation().distance(player.getLocation()) <= 2.0) {
                                    fox.damageNearby(player);
                                }
                            }
                        }
                    });
                }
            });
        }, 0L, 20L);
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.cleanup();
            Bukkit.broadcastMessage(org.bukkit.ChatColor.RED + "FoxWars shutting down. Cleaning up all game entities...");
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
