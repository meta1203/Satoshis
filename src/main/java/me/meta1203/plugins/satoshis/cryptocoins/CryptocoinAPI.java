package me.meta1203.plugins.satoshis.cryptocoins;

import java.util.Map;


public interface CryptocoinAPI {
	public CryptocoinAPI loadEcon(NetworkType networkType);
	public boolean sendCoins(GenericAddress<?> tx, double amount);
	public boolean sendCoinsToMulti(Map<GenericAddress<?>, Double> toSend);
	public void saveWallet();
	public void refreshWallet();
	public GenericAddress<?> generateAddress();
}
