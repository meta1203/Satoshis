package dogecoin.mc.dogecoins;

import dogecoin.mc.dogecoins.database.AccountEntry;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Logger;

public class VaultEconAPI implements Economy {

    private final static Logger log = Logger.getLogger("minecraft");
    private final EconomyResponse noBank = new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Dogecoins does not support banks!");
    private DogecoinsEconAPI economy = null;
    private Plugin plugin = null;

    public VaultEconAPI(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), this.plugin);
        if (economy == null) {
            Plugin dogecoins = this.plugin.getServer().getPluginManager().getPlugin("Dogecoins");
            if (dogecoins != null && dogecoins.isEnabled()) {
                economy = Dogecoins.econ;
                log.info(String.format("[%s][Economy] %s hooked.", this.plugin.getDescription().getName(), "Dogecoins"));
            }
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return economy.addAccount(offlinePlayer.getUniqueId());
    }

    public String currencyNamePlural() {
        return Dogecoins.currencyName;
    }

    public String currencyNameSingular() {
        return Dogecoins.currencyName;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (!Dogecoins.isBuyerResponsible && Dogecoins.salesTax) {
            double tax = economy.priceOfTax(amount);
            economy.addFunds(offlinePlayer.getUniqueId(), amount - tax);
            economy.transferTax(amount);
            return new EconomyResponse(amount - tax, economy.getMoney(offlinePlayer.getUniqueId()), ResponseType.SUCCESS, "");
        }
        return new EconomyResponse(amount, economy.getMoney(offlinePlayer.getUniqueId()), ResponseType.SUCCESS, "");
    }

    public String format(double value) {
        return economy.formatValue(value, false);
    }

    public String getName() {
        return "Dogecoins";
    }

    public int fractionalDigits() {
        return 2;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return Util.loadAccount(offlinePlayer.getUniqueId()).getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String worldName) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double amount) {
        return economy.hasMoney(offlinePlayer.getUniqueId(), amount);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return has(offlinePlayer, amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return Util.testAccount(offlinePlayer.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String worldName) {
        return hasAccount(offlinePlayer);
    }

    public boolean isEnabled() {
        return economy != null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        AccountEntry e = Util.loadAccount(offlinePlayer.getUniqueId());
        double fVal = e.getBalance() - amount;
        if (Dogecoins.isBuyerResponsible && Dogecoins.salesTax) {
            fVal -= economy.priceOfTax(amount);
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw a negative amount");
        }
        if (fVal < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw that much" + (Dogecoins.isBuyerResponsible ? " [Tax is " + economy.formatValue(economy.priceOfTax(amount), false) + "]" : ""));
        }
        e.setBalance(fVal);
        Util.saveAccount(e);
        if (Dogecoins.isBuyerResponsible) {
            economy.transferTax(amount);
        }
        return new EconomyResponse(amount, fVal, ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return withdrawPlayer(offlinePlayer, amount);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String worldName) {
        return createPlayerAccount(offlinePlayer);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String worldName, double amount) {
        return depositPlayer(offlinePlayer, amount);
    }


    /**
     * DEPRECATED
     * Vault has deprecated the functions below. You do not have to provide their functionality.
     */

    @Deprecated
    @Override
    public boolean hasAccount(String playerName) {
        return false;
    }

    @Deprecated
    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }

    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Deprecated
    public double getBalance(String playerName) {
        return 0;
    }

    @Deprecated
    public boolean has(String playerName, double amount) {
        return false;
    }

    @Deprecated
    @Override
    public boolean has(String playerName, String worldName, double dAmount) {
        return false;
    }

    @Deprecated
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double dAmount) {
        return null;
    }

    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double dAmount) {
        return null;
    }

    @Deprecated
    public double getBalance(String playerName, String arg1) {
        return 0;
    }

    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double dAmount) {
        return null;
    }

    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double dAmount) {
        return null;
    }

    @Deprecated
    public boolean createPlayerAccount(String arg0) {
        return false;
    }


    /**
     * BANKS
     */

    public boolean hasBankSupport() {
        return false;
    }

    @Deprecated
    public EconomyResponse isBankMember(String arg0, String arg1) {
        return noBank;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return noBank;
    }

    public EconomyResponse isBankOwner(String arg0, String arg1) {
        return noBank;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return noBank;
    }

    public List<String> getBanks() {
        return null;
    }

    public EconomyResponse bankBalance(String arg0) {
        return noBank;
    }

    public EconomyResponse bankDeposit(String arg0, double arg1) {
        return noBank;
    }

    public EconomyResponse bankHas(String arg0, double arg1) {
        return noBank;
    }

    public EconomyResponse bankWithdraw(String arg0, double arg1) {
        return noBank;
    }

    public EconomyResponse createBank(String arg0, String arg1) {
        return noBank;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    public EconomyResponse deleteBank(String arg0) {
        return noBank;
    }

    public class EconomyServerListener implements Listener {

        VaultEconAPI economy = null;

        public EconomyServerListener(VaultEconAPI economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("Dogecoins");

                if (eco != null && eco.isEnabled()) {
                    economy.economy = Dogecoins.econ;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), "Dogecoins"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("Dogecoins")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), "Dogecoins"));
                }
            }
        }
    }

}
