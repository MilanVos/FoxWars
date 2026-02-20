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
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /fox <spawn|shop|generator|setteam>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "spawn" -> {
                if (args.length < 2) return false;
                Team team = Team.valueOf(args[1].toUpperCase());
                FoxCore fox = new FoxCore(team, player.getLocation());
                fox.spawn();
                plugin.getGameManager().registerFox(team, fox);
                player.sendMessage(ChatColor.GREEN + "Spawned " + team.getName() + " Fox!");
            }
            case "shop" -> {
                Villager shop = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                shop.setCustomName(ChatColor.GOLD + "Fox Shop");
                shop.setCustomNameVisible(true);
                shop.setAI(false);
                player.sendMessage(ChatColor.GREEN + "Spawned Fox Shopkeeper!");
            }
            case "generator" -> {
                if (args.length < 2) return false;
                Generator.Type type = Generator.Type.valueOf(args[1].toUpperCase());
                Generator gen = new Generator(player.getLocation(), type);
                plugin.getGameManager().registerGenerator(gen, plugin);
                player.sendMessage(ChatColor.GREEN + "Spawned " + type.name() + " Generator!");
            }
            case "setteam" -> {
                if (args.length < 3) return false;
                Player target = plugin.getServer().getPlayer(args[1]);
                Team team = Team.valueOf(args[2].toUpperCase());
                if (target != null) {
                    plugin.getGameManager().assignTeam(target, team);
                    player.sendMessage(ChatColor.GREEN + "Set " + target.getName() + " to " + team.getName());
                }
            }
        }

        return true;
    }
}
