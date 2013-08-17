package me.meta1203.plugins.satoshis.bitcoin;

import java.util.List;

import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.common.util.concurrent.FutureCallback;

public class TransactionListener implements FutureCallback<Transaction> {

	public void onFailure(Throwable arg0) {
		(new RuntimeException(arg0)).printStackTrace();
	}

	public void onSuccess(Transaction arg0) {
		parseTransaction(arg0);
	}

	public void parseTransaction(Transaction tx) {
		try {
			double value = Satoshis.econ.bitcoinToInGame(tx
					.getValueSentToMe(Satoshis.bapi.getWallet()));
			List<Address> receivers = Util.getContainedAddress(tx.getOutputs());

			for (Address x : receivers) {
				String pName = Util.searchAddress(x);
				Satoshis.econ.addFunds(pName, value);
				Satoshis.log.warning("Added "
						+ Satoshis.econ.formatValue(value, true) + " to "
						+ pName + "!");
			}

			Satoshis.bapi.saveWallet();
			CoinListener.pending.remove(tx);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
