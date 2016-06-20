package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dogecoin.mc.dogecoins.commands.CommandUtil.action;
import static dogecoin.mc.dogecoins.commands.CommandUtil.error;

public class SendCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dogecoins.transact")) {
            error("You do not have permission for this command!", sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            error("You must be a player to execute this command!", sender);
            return true;
        }
        if (args.length != 2) {
            error("Usage: /transact <player> <amount>", sender);
            return true;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null || !Util.testAccount(player.getUniqueId())) {
            error("That player has not yet joined, or does not exist!", sender);
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            error("Amount must be a number!", sender);
            return true;
        }
        if (Dogecoins.econ.hasMoney(((Player) sender).getUniqueId(), amount) && amount > 0) {
            Dogecoins.econ.transact(((Player) sender).getUniqueId(), player.getUniqueId(), amount);
            action("Sucessfully sent " + Dogecoins.econ.formatValue(amount, true) + " to " + args[0] + "!", sender);
        } else {
            error("Invalid amount to send!", sender);
        }
        return true;
    }

}
