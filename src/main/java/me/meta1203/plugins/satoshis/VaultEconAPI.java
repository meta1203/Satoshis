package me.meta1203.plugins.satoshis;

import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class VaultEconAPI implements Economy {
	private final EconomyResponse noBank = new EconomyResponse(0,0,ResponseType.NOT_IMPLEMENTED, "Satoshis does not support banks!");
	private SatoshisEconAPI economy = null; 
	private final static Logger log = Logger.getLogger("minecraft");
	private Plugin plugin = null;
	
	public VaultEconAPI(Plugin p) {
		plugin = p;
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);
		if (economy == null) {
            Plugin satoshis = plugin.getServer().getPluginManager().getPlugin("Satoshis");
            if (satoshis != null && satoshis.isEnabled()) {
                economy = Satoshis.econ;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), "Satoshis"));
            }
        }
	}
	
    public class EconomyServerListener implements Listener {
        VaultEconAPI economy = null;

        public EconomyServerListener(VaultEconAPI economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("Satoshis");

                if (eco != null && eco.isEnabled()) {
                    economy.economy = Satoshis.econ;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), "Satoshis"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("Satoshis")) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), "Satoshis"));
                }
            }
        }
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

	public boolean createPlayerAccount(String arg0) {
		return economy.addAccount(arg0);
	}

	public String currencyNamePlural() {
		return Satoshis.currencyName;
	}

	public String currencyNameSingular() {
		return Satoshis.currencyName;
	}

	public EconomyResponse deleteBank(String arg0) {
		return noBank;
	}

	public EconomyResponse depositPlayer(String arg0, double arg1) {
		if (!Satoshis.buyerorseller && Satoshis.salesTax) {
			double tax = economy.priceOfTax(arg1);
			economy.addFunds(arg0, arg1-tax);
			economy.transferTax(arg1);
			return new EconomyResponse(arg1-tax, economy.getMoney(arg0), ResponseType.SUCCESS, "");
		}
		return new EconomyResponse(arg1, economy.getMoney(arg0), ResponseType.SUCCESS, "");
	}

	public String format(double arg0) {
		return economy.formatValue(arg0, false);
	}

	public int fractionalDigits() {
		return 2;
	}

	public double getBalance(String arg0) {
		return Util.loadAccount(arg0).getAmount();
	}

	public List<String> getBanks() {
		return null;
	}

	public String getName() {
		return "Satoshis";
	}

	public boolean has(String arg0, double arg1) {
		return economy.hasMoney(arg0, arg1);
	}

	public boolean hasAccount(String arg0) {
		return Util.testAccount(arg0);
	}

	public boolean hasBankSupport() {
		return false;
	}

	public EconomyResponse isBankMember(String arg0, String arg1) {
		return noBank;
	}

	public EconomyResponse isBankOwner(String arg0, String arg1) {
		return noBank;
	}

	public boolean isEnabled() {
		return economy != null;
	}

	public EconomyResponse withdrawPlayer(String arg0, double arg1) {
		AccountEntry e = Util.loadAccount(arg0);
		double fVal = e.getAmount() - arg1;
		if (Satoshis.buyerorseller && Satoshis.salesTax) {
			fVal -= economy.priceOfTax(arg1);
		}
		if (arg1 < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw a negative amount");
		}
		if (fVal < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw that much" + (Satoshis.buyerorseller ? " [Tax is " + economy.formatValue(economy.priceOfTax(arg1), false) + "]" : ""));
		}
		e.setAmount(fVal);
		Util.saveAccount(e);
		if (Satoshis.buyerorseller)
			economy.transferTax(arg1);
		return new EconomyResponse(arg1, fVal, ResponseType.SUCCESS, "");
	}
	
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

	public EconomyResponse depositPlayer(String arg0, String arg1, double arg2) {
		return depositPlayer(arg0, arg2);
	}

	public double getBalance(String arg0, String arg1) {
		return Util.loadAccount(arg0).getAmount();
	}

	public boolean has(String arg0, String arg1, double arg2) {
		return economy.hasMoney(arg0, arg2);
	}

	public boolean hasAccount(String arg0, String arg1) {
		return Util.testAccount(arg0);
	}

	public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
		return withdrawPlayer(arg0, arg2);
	}

}
