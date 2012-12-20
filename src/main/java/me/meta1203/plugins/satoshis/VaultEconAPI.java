package me.meta1203.plugins.satoshis;

import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import net.milkbowl.vault.economy.plugins.Economy_McMoney;

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
	}
	
    public class EconomyServerListener implements Listener {
        VaultEconAPI economy = null;

        public EconomyServerListener(VaultEconAPI economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("McMoney");

                if (eco != null && eco.isEnabled()) {
                    economy.economy = Satoshis.econ;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), "Satoshis"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals("McMoney")) {
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
		// TODO Auto-generated method stub
		return false;
	}

	public String currencyNamePlural() {
		// TODO Auto-generated method stub
		return null;
	}

	public String currencyNameSingular() {
		// TODO Auto-generated method stub
		return null;
	}

	public EconomyResponse deleteBank(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public EconomyResponse depositPlayer(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String format(double arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int fractionalDigits() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getBalance(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<String> getBanks() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean has(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasBankSupport() {
		// TODO Auto-generated method stub
		return false;
	}

	public EconomyResponse isBankMember(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public EconomyResponse isBankOwner(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public EconomyResponse withdrawPlayer(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
