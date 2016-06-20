package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * A command that lets admins assign orphaned BTC in the wallet to player
 * accounts.
 */
public class CreditCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dogecoins.credit")) {
            CommandUtil.error("You do not have permission for this command!", sender);
            return true;
        }

        if (args.length != 2) {
            CommandUtil.error("Usage: /credit <player> <amount>", sender);
            return true;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
        if (player == null || !Util.testAccount(player.getUniqueId())) {
            CommandUtil.error("That player has not yet joined, or does not exist!", sender);
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            CommandUtil.error("Amount must be a number!", sender);
            return true;
        }
        if (amount > 0) {
            Dogecoins.econ.addFunds(player.getUniqueId(), amount);
            CommandUtil.action("Sucessfully credited " + Dogecoins.econ.formatValue(amount, true) + " to " + args[0] + "!", sender);
        } else {
            CommandUtil.error("Invalid amount to credit!", sender);
        }
        return true;
    }

}
