package me.meta1203.plugins.satoshis.bitcoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.bitcoin.core.*;
import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import com.google.bitcoin.core.TransactionConfidence.ConfidenceType;

public class CheckThread extends Thread {
	private List<Transaction> toCheck = new ArrayList<Transaction>();
	private Map<Address, Double> toSend = new HashMap<Address, Double>(); 
	
	private int waitTime = 0;
	private int confirmations = 0;
	
	public CheckThread(int wait, int confirmations) {
		Satoshis.log.info("Checking for " + Integer.toString(confirmations) + " confirmations every " + Integer.toString(wait) + " seconds.");
		waitTime = wait;
		this.confirmations = confirmations;
		toCheck.addAll(Util.loadChecking());
	}
	
	public void run() {
		while (true) {
			check();
			send();
			try {
				synchronized (this) {
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void addSend(Address a, double value) {
		toSend.put(a, value);
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
						Address receiver = current.getOutputs().get(0).getScriptPubKey().getToAddress();
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
	
	private void send() {
		if (Satoshis.fee) {
			for (Entry<Address, Double> current : toSend.entrySet()) {
				Satoshis.bapi.localSendCoins(current.getKey(), current.getValue());
			}
			toSend.clear();
		} else {
			boolean shouldClear = Satoshis.bapi.sendCoinsMulti(toSend);
			if (shouldClear) {
				toSend.clear();
			}
		}
	}

}
