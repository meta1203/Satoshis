package me.meta1203.plugins.satoshis;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.WalletTransaction;

public class Util {
	
	public static Satoshis plugin;
	public static final Logger log = Logger.getLogger("Minecraft");
	
	public static double roundTo(double input, int place) {
		return Math.round(input/(10*place))/(10*place);
	}
	
	public static boolean testAccount(String name) {
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Satoshis");
			plugin = (Satoshis)p;
		}
		AccountEntry ae = plugin.getAccount(name);
		if (ae == null) {
			return false;
		}
		return true;
	}
	
	public static AccountEntry loadAccount(String accName) {
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Satoshis");
			plugin = (Satoshis)p;
		}
		AccountEntry ae = plugin.getAccount(accName);
		if (ae == null) {
			ae = new AccountEntry();
			ae.setPlayerName("accName");
			ae.setAmount(0.0);
		}
		return ae;
	}
	
	public static void saveAccount(AccountEntry ae) {
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Satoshis");
			plugin = (Satoshis)p;
		}
		plugin.saveAccount(ae);
	}
	
	public static void addFunds(String accName, double value) {
		AccountEntry e = loadAccount(accName);
		e.setAmount(e.getAmount() + value);
		saveAccount(e);
	}
	
	public static void subFunds(String accName, double value) {
		AccountEntry e = loadAccount(accName);
		double fVal = e.getAmount() - value;
		if (fVal < 0) {
			fVal = 0;
		}
		saveAccount(e);
	}
	
	public static Address getAddressFromTransaction(Transaction tx) {
		Iterable<WalletTransaction> it = Satoshis.bapi.wallet.getWalletTransactions();
		for (WalletTransaction current : it) {
			// current.getTransaction().
		}
	}
	
}
