package me.meta1203.plugins.satoshis;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

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
	
}
