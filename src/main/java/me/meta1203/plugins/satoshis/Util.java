package me.meta1203.plugins.satoshis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;

public class Util {

	public static Satoshis plugin;
	public static final Logger log = Logger.getLogger("Minecraft");

	public static double roundTo(double input, int place) {
		return Math.round(input * Math.pow(10, place)) / Math.pow(10, place);
	}
	
	public static double getBitcoin(BigInteger raw) {
		return raw.longValue() / Math.pow(10, 8);
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
			ae.setAddr(Satoshis.bapi.genAddress());
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
	
	public static String searchAddress(Address addr) {
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Satoshis");
			plugin = (Satoshis) p;
		}
		AccountEntry ae = plugin.getDatabase().find(AccountEntry.class).where().eq("addr", addr).findUnique();
		if (ae == null) {
			return null;
		}
		return ae.getPlayerName();
	}

	public static void serializeChecking(List<Transaction> toSerialize) {
		File save = new File("plugins/Satoshis/tx.temp");
		PrintWriter pw = null;
		try {
            pw = new PrintWriter(save);
            for (Transaction current : toSerialize) {
                pw.println(current.getHash().toString());
            }
            pw.flush();
            pw.close();
        } catch (Exception ex) {
			ex.printStackTrace();
		}
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
				ret.add(new Transaction(NetworkParameters.prodNet(), 0, new Sha256Hash(strLine)));
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
