package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.SPVBlockStore;
import com.google.bitcoin.store.UnreadableWalletException;

public class BitcoinAPI {

	private Wallet localWallet;
	private SPVBlockStore localBlock;
	private BlockChain localChain;
	private final File walletFile;
	private PeerGroup localPeerGroup = null;
	public static final BigInteger minBitFeeBI = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
	public static final double minBitFee = Util.getBitcoin(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);

	public BitcoinAPI() {
		walletFile = new File("plugins/Satoshis/wallet.wallet");
		try {
			localWallet = Wallet.loadFromFile(walletFile);
			// Satoshis.log.info(localWallet.toString());
		} catch (UnreadableWalletException e) {
			localWallet = new Wallet(Satoshis.network);
		}
		try {
			localBlock = new SPVBlockStore(Satoshis.network, new File(
					"plugins/Satoshis/spv.blockchain"));
			localChain = new BlockChain(Satoshis.network, localWallet,
					localBlock);
		} catch (BlockStoreException ex) {
			ex.printStackTrace();
		}
		localWallet.addEventListener(new CoinListener());
		localPeerGroup = new PeerGroup(Satoshis.network, localChain);
		localPeerGroup.setUserAgent("SatoshisBukkit", "0.2");
		localPeerGroup.addWallet(localWallet);
		localPeerGroup.addPeerDiscovery(new DnsDiscovery(Satoshis.network));
		localPeerGroup.startAndWait();
		localPeerGroup.downloadBlockChain();
	}

	public Address genAddress() {
		ECKey key = new ECKey();
		localWallet.addKey(key);
		return key.toAddress(Satoshis.network);
	}

	@Override
	protected void finalize() throws Throwable {
		localWallet.saveToFile(new File("plugins/Satoshis/wallet.wallet"));
	}

	public boolean localSendCoins(Address a, double value) {
		BigInteger sendAmount = Satoshis.econ.inGameToBitcoin(value);

		Wallet.SendRequest request = Wallet.SendRequest.to(a, sendAmount);
		request.fee = minBitFeeBI;

		if (!localWallet.completeTx(request))
			return false;
		localPeerGroup.broadcastTransaction(request.tx);

		try {
			localWallet.commitTx(request.tx);
		} catch (VerificationException e) {
			e.printStackTrace();
		}
		saveWallet();

		Satoshis.log.warning("Sent transaction: " + request.tx.getHash());
		return true;
	}

	public boolean sendCoinsMulti(Map<Address, Double> toSend) {
		Transaction tx = new Transaction(Satoshis.network);
		double totalSend = 0.0;

		for (Entry<Address, Double> current : toSend.entrySet()) {
			totalSend += current.getValue() / Satoshis.mult;
			tx.addOutput(Satoshis.econ.inGameToBitcoin(current.getValue()),
					current.getKey());
		}

		if (totalSend < 0.01) {
			return false;
		}

		Wallet.SendRequest request = Wallet.SendRequest.forTx(tx);

		if (!localWallet.completeTx(request)) {
			return false;
		} else {
			localPeerGroup.broadcastTransaction(request.tx);
			try {
				localWallet.commitTx(request.tx);
			} catch (VerificationException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	public void saveWallet() {
		try {
			localWallet.saveToFile(walletFile);
			localPeerGroup.stop();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public Wallet getWallet() {
		return localWallet;
	}

	public BlockChain getChain() {
		return localChain;
	}

	public void reloadWallet() {
		localPeerGroup.stop();
		localWallet.clearTransactions(0);
		new File("plugins/Satoshis/spv.blockchain").delete();
		localPeerGroup.start();
		localPeerGroup.downloadBlockChain();
	}
}
