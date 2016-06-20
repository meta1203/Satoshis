package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static dogecoin.mc.dogecoins.commands.CommandUtil.action;
import static dogecoin.mc.dogecoins.commands.CommandUtil.error;

/**
 * A command that lets admins remove in-game currency from circulation.
 */
public class DebitCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dogecoins.debit")) {
            error("You do not have permission for this command!", sender);
            return true;
        }

        if (args.length != 2) {
            error("Usage: /debit <player> <amount>", sender);
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
        if (amount > 0) {
            Dogecoins.econ.subFunds(player.getUniqueId(), amount);
            action("Sucessfully debited " + Dogecoins.econ.formatValue(amount, true) + " from " + args[0] + "!", sender);
        } else {
            error("Invalid amount to debit!", sender);
        }
        return true;
    }

}
