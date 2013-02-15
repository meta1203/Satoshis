package me.meta1203.plugins.satoshis.commands;

import me.meta1203.plugins.satoshis.Satoshis;
import static me.meta1203.plugins.satoshis.commands.CommandUtil.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.bitcoin.core.Address;


public class DepositCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("satoshis.deposit")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg0 instanceof Player) {
			Player player = (Player)arg0;
			String name = player.getName();
			Address alloc = Satoshis.bapi.allocate(name);
			info("Send Bitcoin to the following address:", arg0);
			info(alloc.toString(), arg0);
			info("Blockchain.info link:", arg0);
			info("http://blockchain.info/address/" + alloc.toString(), arg0);
			info("This address will be valid until you deposit.", arg0);
		}
		return true;
	}

}
