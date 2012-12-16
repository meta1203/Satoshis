package me.meta1203.plugins.satoshis.commands;

import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.meta1203.plugins.satoshis.commands.CommandUtil.*;

public class SendCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("satoshis.send")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (!(arg0 instanceof Player)) {
			error("You must be a player to execute this command!", arg0);
			return true;
		}
		Player player = (Player)arg0;
		if (arg3.length != 2) {
			error("Usage: /send <player> <amount>", arg0);
			return true;
		}
		if (!Util.testAccount(arg3[0])) {
			error("That player has not yet joined, or does not exist!", arg0);
			return true;
		}
		double amount = 0.0;
		try {
			amount = Double.parseDouble(arg3[1]);
		} catch (NumberFormatException e) {
			error("Amount must be a number!", arg0);
			return true;
		}
		if (Satoshis.econ.hasMoney(player.getName(), amount) && amount > 0) {
			Satoshis.econ.transact(player.getName(), arg3[0], amount);
			action("Sucessfully sent " + Satoshis.econ.formatValue(amount, true) + " to " +
					arg3[0] + "!", arg0);
		} else {
			error("Invalid amount to send!", arg0);
		}
		return true;
	}

}
