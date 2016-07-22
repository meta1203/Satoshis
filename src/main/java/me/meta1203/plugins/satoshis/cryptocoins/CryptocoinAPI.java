package me.meta1203.plugins.satoshis.cryptocoins;

import java.util.Map;


public interface CryptocoinAPI {
	public void loadEcon(NetworkType networkType, int confirms);
	// Address operations
	public GenericAddress<?> generateAddress();
	public GenericAddress<?> addressContainer();
	public boolean sendCoins(GenericAddress<?> tx, double amount);
	public boolean sendCoinsToMulti(Map<GenericAddress<?>, Double> toSend);
	// Wallet operations
	public void saveWallet();
	public void refreshWallet();
	public double getWalletBalance();
	// Network settings
	public NetworkType getNetworkType();
	public int getConfirms();
	public double getMinimumFee();
}
