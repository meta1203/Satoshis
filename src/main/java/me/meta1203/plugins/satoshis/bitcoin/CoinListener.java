package me.meta1203.plugins.satoshis.bitcoin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import com.google.common.util.concurrent.Futures;

import me.meta1203.plugins.satoshis.Satoshis;

public class CoinListener implements WalletCoinsReceivedEventListener { 
	public static List<Transaction> pending = new ArrayList<Transaction>();
	
	public void onCoinsReceived(Wallet wallet, Transaction tx,
			Coin prevBalance, Coin newBalance) {
		pending.add(tx);
		Futures.addCallback(tx.getConfidence().getDepthFuture(Satoshis.confirms), new TransactionListener()); // TODO: Okay, what!?
	}

}
