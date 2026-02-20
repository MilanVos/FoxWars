package nl.phoenixdev.foxWars.command;

import nl.phoenixdev.foxWars.FoxWars;
import nl.phoenixdev.foxWars.fox.FoxCore;
import nl.phoenixdev.foxWars.game.Team;
import nl.phoenixdev.foxWars.generator.Generator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public class FoxWarsCommand implements CommandExecutor {
    private final FoxWars plugin;

    public FoxWarsCommand(FoxWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("forcestop")) {
                plugin.getGameManager().cleanup();
                plugin.getGameManager().setState(nl.phoenixdev.foxWars.game.GameState.LOBBY);
                if (sender != null) sender.sendMessage(ChatColor.RED + "Game forcefully stopped and cleaned up!");
                return true;
            }
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /fox <setlobby|setspawn|setfox|setshop|addgenerator|forcestop>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "setlobby" -> {
                plugin.getConfigManager().setLocation("lobby", player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Lobby location set!");
            }
            case "setspawn" -> {
                if (args.length < 2) return false;
                Team team = Team.valueOf(args[1].toUpperCase());
                plugin.getConfigManager().setLocation("teams." + team.name() + ".spawn", player.getLocation());
                player.sendMessage(ChatColor.GREEN + team.getName() + " spawn location set!");
            }
            case "setfox" -> {
                if (args.length < 2) return false;
                Team team = Team.valueOf(args[1].toUpperCase());
                plugin.getConfigManager().setLocation("teams." + team.name() + ".fox", player.getLocation());
                player.sendMessage(ChatColor.GREEN + team.getName() + " Fox location set!");
            }
            case "setshop" -> {
                if (args.length < 2) return false;
                Team team = Team.valueOf(args[1].toUpperCase());
                plugin.getConfigManager().setLocation("teams." + team.name() + ".shop", player.getLocation());
                player.sendMessage(ChatColor.GREEN + team.getName() + " Shop location set!");
            }
            case "addgenerator" -> {
                if (args.length < 2) return false;
                Generator.Type type = Generator.Type.valueOf(args[1].toUpperCase());
                plugin.getConfigManager().addGeneratorLocation(type, player.getLocation());
                player.sendMessage(ChatColor.GREEN + type.name() + " Generator added!");
            }
            case "forcestop" -> {
                plugin.getGameManager().cleanup();
                plugin.getGameManager().setState(nl.phoenixdev.foxWars.game.GameState.LOBBY);
                player.sendMessage(ChatColor.RED + "Game forcefully stopped and cleaned up!");
                plugin.getServer().broadcastMessage(ChatColor.RED + player.getName() + " has forcefully stopped the game!");
            }

        }
    return true;
    }
}
