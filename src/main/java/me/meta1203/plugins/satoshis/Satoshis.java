package me.meta1203.plugins.satoshis;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import me.meta1203.plugins.satoshis.bitcoin.BitcoinAPI;
import me.meta1203.plugins.satoshis.bitcoin.CheckThread;
import me.meta1203.plugins.satoshis.commands.*;
import me.meta1203.plugins.satoshis.database.DatabaseScanner;
import me.meta1203.plugins.satoshis.database.SystemCheckThread;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.mcstats.Metrics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.bitcoin.core.NetworkParameters;

public class Satoshis extends JavaPlugin implements Listener {
	// Plugin
	public static String owner = "";
	public static String currencyName = "";
	public static double tax = 0.0;
	public static boolean buyerorseller = false;
	public static boolean salesTax = false;
	public static double mult = 0;
	public static int confirms = 2;
	public static double minWithdraw = 0;
	public static BitcoinAPI bapi = null;
	public static CheckThread checker = null;
	public static Logger log = null;
	public static SatoshisEconAPI econ = null;
	public static VaultEconAPI vecon = null;
	public static DatabaseScanner scanner = null;
	public static NetworkParameters network = null;
	private SystemCheckThread syscheck = null;
	
    public void onDisable() {
    	checker.serialize();
    	bapi.saveWallet();
    }

    public void onEnable() {
    	log = getLogger();
    	setupDatabase();
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	saveConfig();
    	owner = config.getString("satoshis.owner");
    	currencyName = config.getString("satoshis.currency-name");
    	tax = config.getDouble("satoshis.tax");
    	
    	buyerorseller = config.getBoolean("satoshis.is-buyer-responsible");
    	salesTax = config.getBoolean("satoshis.sales-tax");
    	minWithdraw = config.getDouble("bitcoin.min-withdraw");
    	mult = config.getDouble("satoshis.multiplier");
    	network = config.getBoolean("bitcoin.testnet") ? NetworkParameters.testNet3() : NetworkParameters.prodNet();
    	confirms = config.getInt("bitcoin.confirms");
    	
    	// Config loading done!
    	log.info("Satoshis configuration loaded.");
    	
    	checker = new CheckThread(config.getInt("bitcoin.check-interval"), confirms);
    	syscheck = new SystemCheckThread(config.getInt("self-check.delay"), config.getBoolean("self-check.startup"));
    	econ = new SatoshisEconAPI();
    	econ.buyerorseller = buyerorseller;
    	bapi = new BitcoinAPI();
    	scanner = new DatabaseScanner(this);
    	checker.start();
    	syscheck.start();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("deposit").setExecutor(new DepositCommand());
        this.getCommand("withdraw").setExecutor(new WithdrawCommand());
        this.getCommand("money").setExecutor(new MoneyCommand());
        this.getCommand("syscheck").setExecutor(new CheckCommand());
        this.getCommand("transact").setExecutor(new SendCommand());
        this.getCommand("credit").setExecutor(new CreditCommand());
        this.getCommand("debit").setExecutor(new DebitCommand());
        this.getCommand("satoshis").setExecutor(new AdminCommand());
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            log.info("Metrics started!");
        } catch (IOException e) {
        	log.info("Metrics disabled.");
        }
        activateVault();
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Util.saveAccount(Util.loadAccount(event.getPlayer().getName()));
    }

	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(AccountEntry.class);
		return list;
	}

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
	
	private boolean activateVault() {
	    log.info("Attempting to activate Satoshis Vault support...");
		Plugin vault = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		if (vault == null || !(vault instanceof Vault)) {
			log.warning("Vault support disabled.");
			return false;
		}
		vecon = new VaultEconAPI(this);
		getServer().getServicesManager().register(Economy.class, vecon, this, ServicePriority.Highest);
		log.warning("Vault support enabled.");
		return true;
	}
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent e) {
		saveAccount(Util.loadAccount(e.getPlayer().getName()));
	}
}
