package me.meta1203.plugins.satoshis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.bitcoin.core.Address;

public class DepositCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0 instanceof Player) {
			Player player = (Player)arg0;
			String name = player.getName();
			Address alloc = Satoshis.bapi.allocate(name);
			player.sendMessage("Send Bitcoin to the following address:");
			player.sendMessage(alloc.toString());
			player.sendMessage("This address will be valid for 12 hours.");
		}
		return true;
	}

}
