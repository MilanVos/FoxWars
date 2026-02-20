package nl.phoenixdev.foxWars.scoreboard;

import nl.phoenixdev.foxWars.game.GameManager;
import nl.phoenixdev.foxWars.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardService {
    private final GameManager gameManager;

    public ScoreboardService(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void updateScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("foxwars", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "FOX WARS");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = 10;
        obj.getScore(ChatColor.GRAY + "----------------").setScore(score--);
        
        for (Team team : Team.values()) {
            String status = gameManager.isFoxAlive(team) ? ChatColor.GREEN + "✔" : ChatColor.RED + "✘";
            obj.getScore(team.getChatColor() + team.getName() + ": " + status).setScore(score--);
        }

        obj.getScore(" ").setScore(score--);
        Team playerTeam = gameManager.getTeam(player);
        obj.getScore(ChatColor.WHITE + "Your Team: " + (playerTeam != null ? playerTeam.getChatColor() + playerTeam.getName() : "None")).setScore(score--);
        
        obj.getScore(ChatColor.GRAY + "---------------- ").setScore(score);

        player.setScoreboard(board);
    }
}
