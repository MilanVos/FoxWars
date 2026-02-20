package nl.phoenixdev.foxWars.game;

import nl.phoenixdev.foxWars.fox.FoxCore;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {
    private final Map<UUID, Team> playerTeams = new HashMap<>();
    private final Map<Team, FoxCore> teamFoxes = new HashMap<>();
    
    public void assignTeam(Player player, Team team) {
        playerTeams.put(player.getUniqueId(), team);
    }

    public Team getTeam(Player player) {
        return playerTeams.get(player.getUniqueId());
    }

    public void registerFox(Team team, FoxCore fox) {
        teamFoxes.put(team, fox);
    }

    public FoxCore getFox(Team team) {
        return teamFoxes.get(team);
    }

    public boolean isFoxAlive(Team team) {
        FoxCore fox = teamFoxes.get(team);
        return fox != null && fox.isAlive();
    }
}
