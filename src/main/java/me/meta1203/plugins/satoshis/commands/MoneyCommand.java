package me.meta1203.plugins.satoshis.commands;

import me.meta1203.plugins.satoshis.Satoshis;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			String amount = Satoshis.econ.listMoney(((Player)arg0).getName());
			arg0.sendMessage("You have " + amount);
		} else {
			arg0.sendMessage(ChatColor.RED + "You must be a player to execute this command!");
		}
		return true;
	}

}
