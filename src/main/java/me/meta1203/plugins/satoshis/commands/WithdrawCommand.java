package me.meta1203.plugins.satoshis.commands;

import me.meta1203.plugins.satoshis.Satoshis;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.WrongNetworkException;
import static me.meta1203.plugins.satoshis.commands.CommandUtil.*;

public class WithdrawCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("satoshis.withdraw")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg0 instanceof Player) {
			Player player = (Player)arg0;
			
			// Withdraw exact amount
			if (arg3.length == 2) {
				try {
					Address withdrawTo = new Address(NetworkParameters.prodNet(), arg3[0]);
					double withdraw = Double.parseDouble(arg3[1]);
					if (Satoshis.econ.hasMoney(player.getName(), withdraw)) {
						error("Oops! You cannot withdraw more money than you have!", arg0);
						return true;
					}
					Satoshis.checker.addSend(withdrawTo, withdraw-(Satoshis.fee ? Satoshis.econ.minCurrFee : 0.0));
					action("Sending " + Satoshis.econ.formatValue(withdraw-(Satoshis.fee ? Satoshis.econ.minCurrFee : 0.0), false) + " to address " + withdrawTo.toString() + " sucessfully!", arg0);
					if (!Satoshis.fee) {
						info("Group send is active; Once total withdraws => 0.01 BTC, transaction will be finalized.", arg0);
					}
					Satoshis.econ.subFunds(player.getName(), withdraw + (Satoshis.fee ? Satoshis.econ.minCurrFee : 0.0));
				} catch (WrongNetworkException e) {
					error("Oops! That address was for the TestNet!", arg0);
				} catch (AddressFormatException e) {
					error("Oops! Is that the correct address?", arg0);
				} catch (NumberFormatException e) {
					error("Syntax: /withdraw <address> [amount]", arg0);
					error("Amount must be a number!",arg0);
				}
			} else if (arg3.length == 1) {
				try {
					Address withdrawTo = new Address(NetworkParameters.prodNet(), arg3[0]);
					double withdraw = Satoshis.econ.getMoney(player.getName());
					if (withdraw == 0 + withdraw+(Satoshis.fee ? Satoshis.econ.minCurrFee : 0.0)) {
						error("Oops! You have no money in your account!", arg0);
						return true;
					}
					Satoshis.checker.addSend(withdrawTo, withdraw-(Satoshis.fee ? Satoshis.econ.minCurrFee : 0.0));
					action("Sending " + Satoshis.econ.formatValue(withdraw-(Satoshis.fee ? Satoshis.econ.minCurrFee : 0.0), false) + " to address " + withdrawTo.toString() + " sucessfully!", arg0);
					if (!Satoshis.fee) {
						info("Group send is active; Once total withdraws => 0.01 BTC, transaction will be finalized.", arg0);
					}
					Satoshis.econ.subFunds(player.getName(), withdraw);
				} catch (WrongNetworkException e) {
					error("Oops! That address was for the TestNet!", arg0);
				} catch (AddressFormatException e) {
					error("Oops! Is that the correct address?", arg0);
				} catch (NumberFormatException e) {
					error("Syntax: /withdraw <address> [amount]", arg0);
					error("Amount must be a number!",arg0);
				}
			} else {
				error("Syntax: /withdraw <address> [amount]", arg0);
			}
		}
		
		return true;
	}

}
