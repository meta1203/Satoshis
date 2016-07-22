package me.meta1203.plugins.satoshis.bitcoin;

import org.bitcoinj.core.Transaction;

import me.meta1203.plugins.satoshis.cryptocoins.GenericTransaction;

public class BitcoinGenericTransaction implements GenericTransaction<Transaction> {
	private Transaction tx;
	
	public BitcoinGenericTransaction(Transaction tx) {
		this.tx = tx;
	}
	
	public Transaction getTransaction() {
		return tx;
	}

	public void setTransaction(Transaction tx) {
		this.tx = tx;
	}

}
