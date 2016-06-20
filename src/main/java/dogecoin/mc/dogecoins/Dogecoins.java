package dogecoin.mc.dogecoins;

import com.google.common.util.concurrent.Futures;
import dogecoin.mc.dogecoins.commands.*;
import dogecoin.mc.dogecoins.database.AccountDatabase;
import dogecoin.mc.dogecoins.database.DatabaseScanner;
import dogecoin.mc.dogecoins.database.SystemCheckThread;
import dogecoin.mc.dogecoins.dogecoin.CoinListener;
import dogecoin.mc.dogecoins.dogecoin.DogecoinAPI;
import dogecoin.mc.dogecoins.dogecoin.TransactionListener;
import net.milkbowl.vault.economy.Economy;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.libdohj.params.DogecoinMainNetParams;
import org.libdohj.params.DogecoinTestNet3Params;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class Dogecoins extends JavaPlugin implements Listener {

    public static UUID owner;
    public static String currencyName = "";
    public static double tax = 0.0;
    public static boolean isBuyerResponsible = false;
    public static boolean salesTax = false;
    public static int confirms = 1;
    public static double minWithdraw = 0;
    public static DogecoinAPI dogecoinAPI = null;
    public static Logger log = null;
    public static DogecoinsEconAPI econ = null;
    public static VaultEconAPI vaultEcon = null;
    public static DatabaseScanner scanner = null;
    public static NetworkParameters network = null;
    private SystemCheckThread sysCheck = null;

    @Override
    public void onEnable() {
        log = getLogger();
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        owner = Bukkit.getOfflinePlayer(config.getString("dogecoins.owner")).getUniqueId();
        currencyName = config.getString("dogecoins.currency-name");
        tax = config.getDouble("dogecoins.tax");
        isBuyerResponsible = config.getBoolean("dogecoins.is-buyer-responsible");
        salesTax = config.getBoolean("dogecoins.sales-tax");
        minWithdraw = config.getDouble("dogecoins.min-withdraw");
        network = config.getBoolean("dogecoins.testnet") ? DogecoinTestNet3Params.get() : DogecoinMainNetParams.get();
        confirms = config.getInt("dogecoins.confirms");
        sysCheck = new SystemCheckThread(config.getInt("self-check.delay"), config.getBoolean("self-check.startup"));

        AccountDatabase.initialize(this);
        econ = new DogecoinsEconAPI();
        econ.isBuyerResponsible = isBuyerResponsible;
        dogecoinAPI = new DogecoinAPI();
        scanner = new DatabaseScanner(this);
        sysCheck.start();

        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("deposit").setExecutor(new DepositCommand());
        this.getCommand("withdraw").setExecutor(new WithdrawCommand());
        this.getCommand("money").setExecutor(new MoneyCommand());
        this.getCommand("syscheck").setExecutor(new CheckCommand());
        this.getCommand("transact").setExecutor(new SendCommand());
        this.getCommand("credit").setExecutor(new CreditCommand());
        this.getCommand("debit").setExecutor(new DebitCommand());
        this.getCommand("dogecoins").setExecutor(new AdminCommand());

        log.info("Registering Vault support...");
        vaultEcon = new VaultEconAPI(this);
        getServer().getServicesManager().register(Economy.class, vaultEcon, this, ServicePriority.Highest);

        log.info("Dogecoins loaded successfully!");
    }

    @Override
    public void onDisable() {
        AccountDatabase.cleanup(this);
        dogecoinAPI.saveWallet();
        Util.serializeChecking(CoinListener.pending);
    }

//    @Override
//    public List<Class<?>> getDatabaseClasses() {
//        List<Class<?>> list = new ArrayList<>();
//        list.add(AccountEntry.class);
//        return list;
//    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

//    private void setupDatabase() {
//        try {
//            getDatabase().find(AccountEntry.class).findRowCount();
//        } catch (PersistenceException ex) {
//            log.log(Level.INFO, "Installing database for {0} due to first time usage", getDescription().getName());
//            installDDL();
//        }
//    }

//    public AccountEntry getAccount(UUID uuid) {
//        if (uuid != null) {
//            EbeanServer database = getDatabase();
//            return database.find(AccountEntry.class).where().ieq("playerUuid", uuid.toString()).findUnique();
//        } else {
//            log.log(Level.WARNING, "uuid was null when requesting an account");
//            return null;
//        }
//    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        // Before the player has *joined* the game (don't use PlayerJoinEvent!), load/save users econ
        // data so that it's ready for other plugins to use when the player has *joined*.
        Util.saveAccount(Util.loadAccount(e.getPlayer().getUniqueId()));
    }

    public void resetTransactions() {
        List<Transaction> toAdd = Util.loadChecking();
        for (Transaction tx : toAdd) {
            Futures.addCallback(tx.getConfidence().getDepthFuture(Dogecoins.confirms), new TransactionListener());
        }
    }

}
