package nl.phoenixdev.foxWars.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum Team {
    RED(ChatColor.RED, Color.RED, "Red"),
    BLUE(ChatColor.BLUE, Color.BLUE, "Blue"),
    GREEN(ChatColor.GREEN, Color.GREEN, "Green"),
    YELLOW(ChatColor.YELLOW, Color.YELLOW, "Yellow");

    private final ChatColor chatColor;
    private final Color color;
    private final String name;

    Team(ChatColor chatColor, Color color, String name) {
        this.chatColor = chatColor;
        this.color = color;
        this.name = name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
