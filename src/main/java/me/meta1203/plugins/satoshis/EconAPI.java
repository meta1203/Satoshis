package me.meta1203.plugins.satoshis;

public class EconAPI {
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
		Util.saveAccount(e);
	}
	
	private double priceOfTax(double traded) {
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
		Satoshis.log.info("Transaction took place!");
		Satoshis.log.info(playerFrom + " paid " + playerTo + ": $" + value);
	}
	
	public String formatValue(double value) {
		value = Util.roundTo(value, 2);
		return "$" + value;
	}
}
