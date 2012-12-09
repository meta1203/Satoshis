package me.meta1203.plugins.satoshis;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import me.meta1203.plugins.satoshis.bitcoin.BitcoinAPI;
import me.meta1203.plugins.satoshis.bitcoin.CheckThread;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Satoshis extends JavaPlugin implements Listener {
	// Database
	public static String walletFile;
	// Plugin
	public String owner = "";
	public double tax = 0.0;
	public boolean buyerorseller = false;
	public static double mult = 0;
	public static BitcoinAPI bapi = null;
	public static CheckThread checker = null;
	public static Logger log = null;
	
    public void onDisable() {
    }

    public void onEnable() {
    	log = getLogger();
    	setupDatabase();
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	saveConfig();
    	walletFile = config.getString("bitcoin.wallet-file");
    	owner = config.getString("satoshis.owner");
    	tax = config.getDouble("satoshis.tax");
    	buyerorseller = config.getBoolean("satoshis.is-buyer-responsible");
    	mult = config.getDouble("satoshis.multiplier");
    	bapi = new BitcoinAPI();
    	
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome, " + event.getPlayer().getDisplayName() + "!");
    }

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(AccountEntry.class);
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		return true;
	}
	
	private void setupDatabase() {
        try {
            getDatabase().find(AccountEntry.class).findRowCount();
        } catch (PersistenceException ex) {
            log.info("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
	
	public AccountEntry getAccount(String name) {
		return getDatabase().find(AccountEntry.class).where().ieq("playerName", name).findUnique();
	}
	
	public void saveAccount(AccountEntry ae) {
		getDatabase().save(ae);
	}
}

