package me.meta1203.plugins.satoshis.bitcoin;

import java.math.BigInteger;

import me.meta1203.plugins.satoshis.Satoshis;

import com.google.bitcoin.core.AbstractWalletEventListener;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;

public class CoinListener extends AbstractWalletEventListener {

	@Override
	public void onCoinsReceived(Wallet wallet, Transaction tx,
			BigInteger prevBalance, BigInteger newBalance) {
		Satoshis.checker.addCheckTransaction(tx);
	}

}
