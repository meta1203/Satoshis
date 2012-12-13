package me.meta1203.plugins.satoshis.commands;

import java.util.Map;

import me.meta1203.plugins.satoshis.Satoshis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.bitcoin.core.Address;

import static me.meta1203.plugins.satoshis.commands.CommandUtil.*;

public class AdminCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		info("INFO:", arg0);
		info("Allocated:",arg0);
		for (Map.Entry<Address, String> current : Satoshis.bapi.allocatedAddresses.entrySet()) {
			info("	" + current.getKey().toString() + " -> " + current.getValue(), arg0);
		}
		info("Unallocated:", arg0);
		for (Address current : Satoshis.bapi.unallocatedAddresses) {
			info("	" + current.toString(), arg0);
		}
		return true;
	}
}
