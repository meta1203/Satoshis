package me.meta1203.plugins.satoshis.commands;

import static me.meta1203.plugins.satoshis.commands.CommandUtil.*;
import me.meta1203.plugins.satoshis.Satoshis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		
		if (!arg0.hasPermission("satoshis.money")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg0 instanceof Player) {
			String amount = Satoshis.econ.listMoney(((Player)arg0).getName());
			info("You have " + amount, arg0);
		} else {
			error("You must be a player to execute this command!", arg0);
		}
		return true;
	}

}
