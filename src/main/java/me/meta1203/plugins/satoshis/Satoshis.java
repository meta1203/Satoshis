package me.meta1203.plugins.satoshis;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Satoshis extends JavaPlugin implements Listener {
	// Database
	public int port = 0;
	public String ip = "";
	public List<String> addresses = null;
	public String user = "";
	public String pass = "";
	// Plugin
	public String owner = "";
	public double tax = 0.0;
	public boolean buyerorseller = false;
	
    public void onDisable() {
    }

    public void onEnable() {
    	setupDatabase();
    	FileConfiguration config = getConfig();
    	config.options().copyDefaults(true);
    	saveConfig();
    	addresses = config.getStringList("bitcoind.addresses");
    	ip = config.getString("bitcoind.ip");
    	port = config.getInt("bitcoind.port");
    	user = config.getString("bitcoind.username");
    	pass = config.getString("bitcoind.password");
    	owner = config.getString("satoshis.owner");
    	tax = config.getDouble("satoshis.tax");
    	buyerorseller = config.getBoolean("satoshis.is-buyer-responsible");
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
            System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }
}

