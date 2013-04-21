package me.meta1203.plugins.satoshis.bitcoin;

import java.util.ArrayList;
import java.util.List;

import com.google.bitcoin.core.*;
import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import com.google.bitcoin.core.TransactionConfidence.ConfidenceType;

public class CheckThread extends Thread {
	private List<Transaction> toCheck = new ArrayList<Transaction>();

	private int waitTime = 0;
	private int confirmations = 0;

	public CheckThread(int wait, int confirmations) {
		Satoshis.log.info("Checking for " + Integer.toString(confirmations) + " confirmations every " + Integer.toString(wait) + " seconds.");
		waitTime = wait;
		this.confirmations = confirmations;
		List<Transaction> toAdd = Util.loadChecking();
		Satoshis.log.info("Adding " + toAdd.size() + " old transactions to the check pool!");
		for (Transaction current : toAdd) {
			Satoshis.log.info("Added: " + current.getHashAsString());
			toCheck.add(current);
		}
	}

	public void run() {
		while (true) {
			check();
			try {
				synchronized (this) {
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void addCheckTransaction(Transaction tx) {
		toCheck.add(tx);
		Satoshis.log.warning("Added transaction " + tx.getHashAsString() + " to check pool!");
	}

	public synchronized void serialize() {
		Util.serializeChecking(toCheck);
	}

	// Loop checks and outputs

	private void check() {
		synchronized (this) {
			List<Transaction> toRemove = new ArrayList<Transaction>();
			for (Transaction current : toCheck) {
				if (!current.getConfidence().getConfidenceType().equals(ConfidenceType.BUILDING)) {
					continue;
				}
				int conf = current.getConfidence().getDepthInBlocks();
				if (conf >= confirmations) {
					try {
						double value = Satoshis.econ.bitcoinToInGame(current.getValueSentToMe(Satoshis.bapi.getWallet()));
						Address receiver = Util.getContainedAddress(current.getOutputs());
						String pName = Util.searchAddress(receiver);

						Satoshis.econ.addFunds(pName, value);
						Satoshis.log.warning("Added " + Satoshis.econ.formatValue(value, true) + " to " + pName + "!");
						Satoshis.bapi.saveWallet();
					} catch (ScriptException e) {
						Satoshis.log.warning("Missed transaction due to ScriptException!");
						e.printStackTrace();
					}
					toRemove.add(current);
				}
			}
			toCheck.removeAll(toRemove);
		}
	}

}
