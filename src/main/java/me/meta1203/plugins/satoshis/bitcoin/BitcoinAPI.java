package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

public class BitcoinAPI {

	private Wallet localWallet;
	private SPVBlockStore localBlock;
	private BlockChain localChain;
	private final File walletFile;
	private PeerGroup localPeerGroup = null;
	public static final Coin minBitFeeBI = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
	public static final double minBitFee = Util.getBitcoin(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);

	public BitcoinAPI() {
		walletFile = new File("plugins/Satoshis/wallet.wallet");
		try {
			localWallet = Wallet.loadFromFile(walletFile);
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
		Satoshis.log.info("Connecting to peers...");
		localPeerGroup.startAndWait(); // TODO: Figure out what startAndWait was changed to in bitcoinj 0.14.3
		Satoshis.log.info("Downloading Blockchain...");
		localPeerGroup.downloadBlockChain();
		Satoshis.log.info("Done, loading the rest of the plugin...");
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
		Coin sendAmount = Satoshis.econ.inGameToBitcoin(value);

		Wallet.SendRequest request = Wallet.SendRequest.to(a, sendAmount); // TODO: Figure out what Wallet.SendRequest was changed to in bitcoinj 0.14.3
		request.fee = minBitFeeBI;

		localPeerGroup.broadcastTransaction(request.tx);

		try {
			localWallet.commitTx(request.tx);
		} catch (VerificationException e) {
			e.printStackTrace();
		}
		saveWallet();

		Satoshis.log.log(Level.WARNING, "Sent transaction: {0}", request.tx.getHash());
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

		Wallet.SendRequest request = Wallet.SendRequest.forTx(tx); // TODO: Figure out what Wallet.SendRequest was changed to in bitcoinj 0.14.3

		localPeerGroup.broadcastTransaction(request.tx);
		try {
			localWallet.commitTx(request.tx);
		} catch (VerificationException e) {
			e.printStackTrace();
		}
		return true;
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
