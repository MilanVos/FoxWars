package nl.phoenixdev.foxWars.game;

import nl.phoenixdev.foxWars.FoxWars;
import nl.phoenixdev.foxWars.fox.FoxCore;
import nl.phoenixdev.foxWars.generator.Generator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameManager {
    private final Map<UUID, Team> playerTeams = new HashMap<>();
    private final Map<Team, FoxCore> teamFoxes = new HashMap<>();
    private final List<Generator> generators = new ArrayList<>();
    private final List<Villager> shops = new ArrayList<>();
    private GameState state = GameState.LOBBY;
    
    public void setState(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public void assignTeam(Player player, Team team) {
        playerTeams.put(player.getUniqueId(), team);
    }

    public Team getTeam(Player player) {
        return playerTeams.get(player.getUniqueId());
    }

    public void registerFox(Team team, FoxCore fox) {
        teamFoxes.put(team, fox);
    }

    public void registerGenerator(Generator generator, JavaPlugin plugin) {
        generators.add(generator);
        generator.start(plugin);
    }

    public FoxCore getFox(Team team) {
        return teamFoxes.get(team);
    }

    public boolean isFoxAlive(Team team) {
        FoxCore fox = teamFoxes.get(team);
        return fox != null && fox.isAlive();
    }

    public void startGame(FoxWars plugin) {
        this.state = GameState.IN_GAME;
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        int teamIndex = 0;
        
        for (Player player : players) {
            Team team = Team.values()[teamIndex % Team.values().length];
            assignTeam(player, team);
            
            Location spawn = plugin.getConfigManager().getLocation("teams." + team.name() + ".spawn");
            if (spawn != null) player.teleport(spawn);
            
            teamIndex++;
        }

        for (Team team : Team.values()) {
            Location foxLoc = plugin.getConfigManager().getLocation("teams." + team.name() + ".fox");
            if (foxLoc != null) {
                FoxCore fox = new FoxCore(team, foxLoc);
                fox.spawn();
                registerFox(team, fox);
            }

            Location shopLoc = plugin.getConfigManager().getLocation("teams." + team.name() + ".shop");
            if (shopLoc != null) {
                Villager shop = (Villager) shopLoc.getWorld().spawnEntity(shopLoc, EntityType.VILLAGER);
                shop.setCustomName(ChatColor.GOLD + "Fox Shop");
                shop.setAI(false);
                registerShop(shop);
            }
        }

        for (Generator.Type type : Generator.Type.values()) {
            for (Location loc : plugin.getConfigManager().getGeneratorLocations(type)) {
                registerGenerator(new Generator(loc, type), plugin);
            }
        }
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "The FoxWars has begun!");
    }

    public Collection<FoxCore> getFoxes() {
        return teamFoxes.values();
    }

    public void registerShop(Villager villager) {
        shops.add(villager);
    }

    public void checkWinner(FoxWars plugin) {
        if (state != GameState.IN_GAME) return;

        List<Team> activeTeams = new ArrayList<>();
        for (Team team : Team.values()) {
            boolean hasPlayers = Bukkit.getOnlinePlayers().stream()
                    .anyMatch(p -> getTeam(p) == team && !p.isDead() && p.getAllowFlight() == false);
            
            if (isFoxAlive(team) || hasPlayers) {
                activeTeams.add(team);
            }
        }

        if (activeTeams.size() <= 1) {
            Team winner = activeTeams.isEmpty() ? null : activeTeams.get(0);
            endGame(winner, plugin);
        }
    }

    private void endGame(Team winner, FoxWars plugin) {
        this.state = GameState.ENDING;
        String message = winner != null ? winner.getChatColor() + winner.getName() + " Team won the game!" : ChatColor.YELLOW + "Game ended in a draw!";
        Bukkit.broadcastMessage(ChatColor.GOLD + "================================");
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage(ChatColor.GOLD + "================================");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer(ChatColor.YELLOW + "Game Restarting..."));
            cleanup();
            state = GameState.LOBBY;
        }, 200L);
    }

    public void cleanup() {
        generators.forEach(Generator::stop);
        generators.clear();
        
        shops.forEach(shop -> {
            if (shop != null && !shop.isDead()) {
                shop.remove();
            }
        });
        shops.clear();
        
        teamFoxes.values().forEach(fox -> {
            if (fox.getEntity() != null && !fox.getEntity().isDead()) {
                fox.getEntity().remove();
            }
        });
        teamFoxes.clear();
        
        playerTeams.clear();
    }
}
