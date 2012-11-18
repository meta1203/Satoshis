package me.meta1203.plugins.satoshis.bitcoin;

import java.math.BigInteger;

import me.meta1203.plugins.satoshis.AccountEntry;
import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import com.google.bitcoin.core.AbstractWalletEventListener;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;

public class CoinListener extends AbstractWalletEventListener {

	@Override
	public void onCoinsReceived(Wallet wallet, Transaction tx,
			BigInteger prevBalance, BigInteger newBalance) {
		Address to = null;
		try {
			to = tx.getOutputs().get(0).getScriptPubKey().getToAddress();
		} catch (ScriptException e) {
			e.printStackTrace();
			return;
		}
		if (BitcoinAPI.allocatedAddresses.containsKey(to)) {
			int data = tx.getValueSentToMe(wallet).intValue();
			AccountEntry entry = Util.loadAccount(BitcoinAPI.allocatedAddresses.get(to));
			entry.setAmount(entry.getAmount() + (data*Satoshis.mult));
		}
	}

}
