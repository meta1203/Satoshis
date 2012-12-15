package me.meta1203.plugins.satoshis.bitcoin;

import java.util.ArrayList;
import java.util.List;

import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence.ConfidenceType;

public class CheckThread extends Thread {
	private static List<Transaction> toCheck = new ArrayList<Transaction>();
	private int waitTime = 0;
	private int confirmations = 0;
	
	public CheckThread(int wait, int confirmations) {
		waitTime = wait;
		this.confirmations = confirmations;
		toCheck.addAll(Util.loadChecking());
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				List<Transaction> toRemove = new ArrayList<Transaction>();
				for (Transaction current : toCheck) {
					if (!current.getConfidence().getConfidenceType().equals(ConfidenceType.BUILDING)) {
						continue;
					}
					int conf = current.getConfidence().getDepthInBlocks(Satoshis.bapi.chain);
					if (conf >= confirmations) {
						double value = current.getValueSentToMe(Satoshis.bapi.wallet).longValue()/Math.pow(10, 8);
						try {
							if (Satoshis.bapi.allocatedAddresses.containsKey(current.getOutputs().get(0).getScriptPubKey().getToAddress())) {
								String pName = Satoshis.bapi.allocatedAddresses.get(current.getOutputs().get(0).getScriptPubKey().getToAddress());
								Address reciver = current.getOutputs().get(0).getScriptPubKey().getToAddress();
								Satoshis.econ.addFunds(pName, value*Satoshis.mult);
								Satoshis.log.warning("Added $" + value*Satoshis.mult + " to " + pName + "!");
								Satoshis.bapi.saveWallet();
								// Remove allocations
								Satoshis.bapi.deallocate(reciver);
							}
						} catch (ScriptException e) {
							e.printStackTrace();
						}
						toRemove.add(current);
					}
				}
				toCheck.removeAll(toRemove);
			}
			try {
				synchronized (this) {
					Satoshis.log.info("Waiting for: " + this.waitTime + " seconds.");
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void addTransaction(Transaction tx) {
		toCheck.add(tx);
		System.out.println("Added transaction " + tx.getHashAsString() + " to check pool!");
	}

	public synchronized void serialize() {
		Util.serializeChecking(toCheck);
	}

}
