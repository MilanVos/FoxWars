package nl.phoenixdev.foxWars.command;

import nl.phoenixdev.foxWars.game.Team;
import nl.phoenixdev.foxWars.generator.Generator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FoxWarsTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("setlobby", "setspawn", "setfox", "setshop", "addgenerator"), args[0]);
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("setspawn") || sub.equals("setfox") || sub.equals("setshop")) {
                return filter(Arrays.stream(Team.values()).map(Enum::name).collect(Collectors.toList()), args[1]);
            }
            if (sub.equals("addgenerator")) {
                return filter(Arrays.stream(Generator.Type.values()).map(Enum::name).collect(Collectors.toList()), args[1]);
            }
        }

        return new ArrayList<>();
    }

    private List<String> filter(List<String> list, String input) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
