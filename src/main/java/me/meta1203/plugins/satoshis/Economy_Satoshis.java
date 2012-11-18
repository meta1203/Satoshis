package me.meta1203.plugins.satoshis;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Economy_Satoshis implements Economy {
	private boolean enabled = false;
	private Plugin plugin;
	private Satoshis econ;
	
	public Economy_Satoshis(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new EconomyServerListener(this), plugin);

        if (econ == null) {
            Plugin econ = plugin.getServer().getPluginManager().getPlugin("Satoshis");
            if (econ != null && econ.isEnabled()) {
                this.econ = (Satoshis) econ;
            }
        }
        
    }
	
	public class EconomyServerListener implements Listener {
        Economy_Satoshis economy = null;

        public EconomyServerListener(Economy_Satoshis economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.econ == null) {
                Plugin eco = plugin.getServer().getPluginManager().getPlugin("Satoshis");

                if (eco != null && eco.isEnabled()) {
                    economy.econ = (Satoshis) eco;
                    Util.log.info("[Satoshis] Vault hooked");
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.econ != null) {
                if (event.getPlugin().getDescription().getName().equals("Satoshis")) {
                    economy.econ = null;
                    Util.log.info("[Satoshis] Vault unhooked");
                }
            }
        }
    }
	

	@Override
	public EconomyResponse bankBalance(String arg0) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String arg0, double arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String arg0, double arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String arg0, double arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public EconomyResponse createBank(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public boolean createPlayerAccount(String arg0) {
		Util.loadAccount(arg0);
		return true;
	}

	@Override
	public String currencyNamePlural() {
		return "Satoshis";
	}

	@Override
	public String currencyNameSingular() {
		return "Satoshis";
	}

	@Override
	public EconomyResponse deleteBank(String arg0) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public EconomyResponse depositPlayer(String arg0, double arg1) {
		AccountEntry ae = Util.loadAccount(arg0);
		double first = ae.getAmount();
		ae.setAmount(first + arg1);
		Util.saveAccount(ae);
		return new EconomyResponse(arg1, ae.getAmount(), ResponseType.SUCCESS, String.format("Added %d to %s's account!", arg1, arg0));
	}

	@Override
	public String format(double arg0) {
		return String.format(arg0 + " %s", this.currencyNameSingular());
	}

	@Override
	public int fractionalDigits() {
		return 0;
	}

	@Override
	public double getBalance(String arg0) {
		return Util.loadAccount(arg0).getAmount();
	}

	@Override
	public List<String> getBanks() {
		return null;
	}

	@Override
	public String getName() {
		return "Satoshis";
	}

	@Override
	public boolean has(String arg0, double arg1) {
		if (Util.loadAccount(arg0).getAmount() >= arg1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasAccount(String arg0) {
		return Util.testAccount(arg0);
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Satoshis does not support bank accounts!");
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public EconomyResponse withdrawPlayer(String arg0, double arg1) {
		AccountEntry ae = Util.loadAccount(arg0);
		double first = ae.getAmount();
		if (first - arg1 < 0) {
			return new EconomyResponse(arg1, arg1, ResponseType.FAILURE, "Not enough money!");
		}
		ae.setAmount(first - arg1);
		Util.saveAccount(ae);
		return new EconomyResponse(arg1, ae.getAmount(), ResponseType.SUCCESS, String.format("Added %d to %s's account!", arg1, arg0));
	}

}
