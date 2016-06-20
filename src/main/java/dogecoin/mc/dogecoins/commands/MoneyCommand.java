package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dogecoins.money")) {
            CommandUtil.error("You do not have permission for this command!", sender);
            return true;
        }

        if (sender instanceof Player) {
            if (args.length == 1) {
                if (Util.testAccount(((Player) sender).getUniqueId())) {
                    CommandUtil.error("Player not found!", sender);
                    return true;
                }
                String amount = Dogecoins.econ.formatValue(Util.loadAccount(((Player) sender).getUniqueId()).getBalance(), true);
                CommandUtil.info(args[0] + " has " + amount, sender);
            } else {
                String amount = Dogecoins.econ.listMoney(((Player) sender).getUniqueId());
                CommandUtil.info("You have " + amount, sender);
                return true;
            }
        } else {
            if (args.length == 1) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if (player != null) {
                    String amount = Dogecoins.econ.listMoney(player.getUniqueId());
                    if (amount != null) {
                        CommandUtil.info(args[0] + " has " + amount, sender);
                    }
                }
                CommandUtil.error("Player not found!", sender);
                return true;
            } else {
                CommandUtil.error("Syntax: money <player>", sender);
            }
        }
        return true;
    }

}
