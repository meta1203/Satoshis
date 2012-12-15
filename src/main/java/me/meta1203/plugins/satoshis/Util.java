package me.meta1203.plugins.satoshis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Transaction;

public class Util {

	public static Satoshis plugin;
	public static final Logger log = Logger.getLogger("Minecraft");

	public static double roundTo(double input, int place) {
		return Math.round(input * Math.pow(10, place)) / Math.pow(10, place);
	}

	public static boolean testAccount(String name) {
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Satoshis");
			plugin = (Satoshis) p;
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
			plugin = (Satoshis) p;
		}
		AccountEntry ae = plugin.getAccount(accName);
		if (ae == null) {
			ae = new AccountEntry();
			ae.setPlayerName(accName);
			ae.setAmount(0.0);
		}
		return ae;
	}

	public static void saveAccount(AccountEntry ae) {
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Satoshis");
			plugin = (Satoshis) p;
		}
		plugin.saveAccount(ae);
	}

	public static void serializeChecking(List<Transaction> toSerialize) {
		File save = new File("plugins/Satoshis/tx.temp");
		PrintWriter out = null;
		try {
			out = new PrintWriter(save);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (Transaction current : toSerialize) {
			out.println(String.valueOf(current.bitcoinSerialize()));
		}
		out.flush();
		out.close();
	}

	public static List<Transaction> loadChecking() {
		File open = new File("plugins/Satoshis/tx.temp");
		List<Transaction> ret = new ArrayList<Transaction>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(open));
		} catch (FileNotFoundException e) {
			return ret;
		}
		String strLine;
		try {
			while ((strLine = in.readLine()) != null) {
				try {
					ret.add(new Transaction(NetworkParameters.prodNet(), strLine.getBytes()));
				} catch (ProtocolException e) {
					Satoshis.log.severe("Failed to parse Transaction!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static Satoshis retrieveInstance() {
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Satoshis");
			plugin = (Satoshis) p;
		}
		return plugin;
	}
}
