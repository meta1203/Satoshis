package me.meta1203.plugins.satoshis.commands;

import java.util.Map;

import me.meta1203.plugins.satoshis.Satoshis;
import static me.meta1203.plugins.satoshis.commands.CommandUtil.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;

public class AdminCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("satoshis.admin")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg3.length != 1) {
			error("Syntax: /admin <info>|<reset>", arg0);
			return true;
		}
		if (arg3[0].equalsIgnoreCase("info"))
			printInfo(arg0);
		else if (arg3[0].equalsIgnoreCase("reset"))
			Satoshis.bapi.reloadWallet();
		else
			error("Syntax: /admin <info>|<reset>", arg0);
		
		return true;
	}
	
	private void printInfo(CommandSender arg0) {
		info("INFO:", arg0);
		info("Wallet:", arg0);
		
		Wallet tmp = Satoshis.bapi.getWallet();
		info("Total balance: " + tmp.getBalance().longValue() + " Satoshi", arg0);
		info("Recent transactions:", arg0);
		for (Transaction t : tmp.getRecentTransactions(3, false)) {
			try {
				info(t.getHashAsString() + " value: +" + t.getValueSentToMe(tmp) + ", -" + t.getValueSentFromMe(tmp), arg0);
			} catch (ScriptException e) {
				error("Transaction " + t.getHashAsString() + " errored out!", arg0);
			}
		}
		
		info("Allocated:",arg0);
		for (Map.Entry<Address, String> current : Satoshis.bapi.allocatedAddresses.entrySet()) {
			info(current.getKey().toString() + " -> " + current.getValue(), arg0);
		}
		info("Unallocated:", arg0);
		for (Address current : Satoshis.bapi.unallocatedAddresses) {
			info(current.toString(), arg0);
		}
	}
}
