package me.meta1203.plugins.satoshis.bitcoin;

import java.util.ArrayList;
import java.util.List;

import me.meta1203.plugins.satoshis.Satoshis;

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
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		while (true) {
			for (Transaction current : toCheck) {
				if (!current.getConfidence().getConfidenceType().equals(ConfidenceType.BUILDING)) {
					continue;
				}
				int conf = current.getConfidence().getDepthInBlocks(Satoshis.bapi.chain);
				if (conf >= confirmations) {
					double value = current.getValueSentToMe(Satoshis.bapi.wallet).longValue();
					try {
						if (Satoshis.bapi.allocatedAddresses.containsKey(current)) {
							String pName = Satoshis.bapi.allocatedAddresses.get(current.getOutputs().get(0).getScriptPubKey().getToAddress());
							Address reciver = current.getOutputs().get(0).getScriptPubKey().getToAddress();
							Satoshis.econ.addFunds(pName, (value/Math.pow(10, 8))*Satoshis.mult);
							
							// Remove allocations
							Satoshis.bapi.unallocatedAddresses.add(reciver);
							Satoshis.bapi.allocatedAddresses.remove(reciver);
						} else {
							if (Satoshis.bapi.sendCoins(current.getInputs().get(0).getFromAddress(), value/Math.pow(10, 8)))
								Satoshis.log.warning("Sent " + value + " Bitcoin back for unallocated address!");
						}
					} catch (ScriptException e) {
						e.printStackTrace();
					}
					toCheck.remove(current);
				}
			}
			try {
				synchronized (this) {
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addTransaction(Transaction tx) {
		toCheck.add(tx);
		System.out.println("Added transaction " + tx.getHashAsString() + " to check pool!");
	}

}
