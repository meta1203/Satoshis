package me.meta1203.plugins.satoshis.bitcoin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.AbstractWalletEventListener;

import com.google.common.util.concurrent.Futures;

import me.meta1203.plugins.satoshis.Satoshis;

public class CoinListener extends AbstractWalletEventListener { // TODO: Figure out what AbstractWalletEventListener was changed to in bitcoinj 0.14.3
	public static List<Transaction> pending = new ArrayList<Transaction>();
	
	@Override
	public void onCoinsReceived(Wallet wallet, Transaction tx,
			BigInteger prevBalance, BigInteger newBalance) {
		pending.add(tx);
		Futures.addCallback(tx.getConfidence().getDepthFuture(Satoshis.confirms), new TransactionListener());
	}

}
