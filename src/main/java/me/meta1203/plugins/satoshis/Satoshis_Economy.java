package me.meta1203.plugins.satoshis;

import java.util.List;

import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class Satoshis_Economy implements Economy {
	private boolean enabled = false;
	private Satoshis plugin;
	
	public Satoshis_Economy(Plugin p) {
		Plugin plag = plugin.getServer().getPluginManager().getPlugin("Satoshis");
		if (plag != null) {
			plugin = (Satoshis)plag;
			enabled = true;
		}
	}
	

	@Override
	public EconomyResponse bankBalance(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse bankDeposit(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse bankHas(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse bankWithdraw(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse createBank(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean createPlayerAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String currencyNamePlural() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String currencyNameSingular() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse deleteBank(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EconomyResponse depositPlayer(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String format(double arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int fractionalDigits() {
		return -1;
	}

	@Override
	public double getBalance(String arg0) {
		return 0;
	}

	@Override
	public List<String> getBanks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean has(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAccount(String arg0) {
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}

}
