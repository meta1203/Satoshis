package me.meta1203.plugins.satoshis;

import java.math.BigInteger;

public class SatoshisEconAPI {
	public final double minCurrFee = 0.0005 * Satoshis.mult;
	public boolean buyerorseller = true;
	
	public void setFunds(String accName, double value) {
		AccountEntry e = Util.loadAccount(accName);
		e.setAmount(value);
		Util.saveAccount(e);
	}
	
	public void addFunds(String accName, double value) {
		AccountEntry e = Util.loadAccount(accName);
		e.setAmount(e.getAmount() + value);
		Util.saveAccount(e);
	}
	
	public void subFunds(String accName, double value) {
		AccountEntry e = Util.loadAccount(accName);
		double fVal = e.getAmount() - value;
		if (fVal < 0) {
			fVal = 0;
		}
		e.setAmount(fVal);
		Util.saveAccount(e);
	}
	
	public double priceOfTax(double traded) {
		return traded * (Satoshis.tax/100);
	}
	
	public void transact(String playerFrom, String playerTo, double value) {
		double tax = priceOfTax(value);
		if (Satoshis.buyerorseller) {
			subFunds(playerFrom, value+tax);
			addFunds(playerTo, value);
		} else {
			subFunds(playerFrom, value);
			addFunds(playerTo, value-tax);
		}
		addFunds(Satoshis.owner, tax);
		Satoshis.log.info("Transaction took place!");
		Satoshis.log.info(playerFrom + " paid " + playerTo + ": " + formatValue(value, true));
	}
	
	public String formatValue(double value, boolean exact) {
		if (exact)
			value = Util.roundTo(value, 2);
		return value + " " + Satoshis.currencyName;
	}
	
	public String listMoney(String player) {
		Util.saveAccount(Util.loadAccount(player));
		return formatValue(Util.loadAccount(player).getAmount(), true);
	}
	
	public boolean addAccount(String player) {
		if (Util.testAccount(player)) {
			return false;
		} else {
			Util.saveAccount(Util.loadAccount(player));
			return true;
		}
	}
	
	public double getMoney(String player) {
		return Util.loadAccount(player).getAmount();
	}
	
	public boolean hasMoney(String player, double amount) {
		double has = Util.loadAccount(player).getAmount();
		return has >= amount;
	}
	
	public BigInteger inGameToBitcoin(double amount) {
		return BigInteger.valueOf((long)(amount * Math.pow(10, 8)/Satoshis.mult));
	}
	
	public double bitcoinToInGame(BigInteger amount) {
		return (amount.longValue() / Math.pow(10, 8)) * Satoshis.mult;
	}
}
