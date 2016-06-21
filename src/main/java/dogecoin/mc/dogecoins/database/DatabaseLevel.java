package dogecoin.mc.dogecoins.database;

import org.bukkit.ChatColor;

public enum DatabaseLevel {

    WARNING(ChatColor.YELLOW), SEVERE(ChatColor.RED),
    UNDER(ChatColor.AQUA), PERFECT(ChatColor.GREEN);

    private ChatColor color;

    private DatabaseLevel(ChatColor warn) {
        color = warn;
    }

    public ChatColor getColor() {
        return color;
    }
}
