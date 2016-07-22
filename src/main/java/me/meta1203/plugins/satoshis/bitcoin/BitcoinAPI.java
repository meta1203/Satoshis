package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
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
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;
import me.meta1203.plugins.satoshis.cryptocoins.CryptocoinAPI;
import me.meta1203.plugins.satoshis.cryptocoins.GenericAddress;
import me.meta1203.plugins.satoshis.cryptocoins.NetworkType;

public class BitcoinAPI implements CryptocoinAPI  {

	private Wallet localWallet;
	private SPVBlockStore localBlock;
	private BlockChain localChain;
	private final File walletFile;
	private PeerGroup localPeerGroup = null;
	private NetworkType netType = null;
	private int confirms = 0;
	public final Coin minBitFeeBI = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
	public final double minBitFee = Util.getCrypto(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);

	public BitcoinAPI() {
		walletFile = new File("plugins/Satoshis/wallet.wallet");
	}

	@Override
	protected void finalize() throws Throwable {
		localWallet.saveToFile(new File("plugins/Satoshis/wallet.wallet"));
	}

	public void saveWallet() {
		try {
			localWallet.saveToFile(walletFile);
			localPeerGroup.stop();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

//	public Wallet getWallet() {
//		return localWallet;
//	}
//
//	public BlockChain getChain() {
//		return localChain;
//	}

	public void loadEcon(NetworkType networkType, int confirms) {
		netType = networkType;
		this.confirms = confirms;
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
		localWallet.addCoinsReceivedEventListener(new CoinListener());
		localPeerGroup = new PeerGroup(Satoshis.network, localChain);
		localPeerGroup.setUserAgent("SatoshisBukkit", "0.3");
		localPeerGroup.addWallet(localWallet);
		localPeerGroup.addPeerDiscovery(new DnsDiscovery(Satoshis.network));
		Satoshis.log.info("Connecting to peers...");
		localPeerGroup.startAsync();
		Satoshis.log.info("Downloading Blockchain...");
		localPeerGroup.downloadBlockChain();
		Satoshis.log.info("Done, loading the rest of the plugin...");
	}

	public boolean sendCoins(GenericAddress<?> tx, double amount) {
		Coin sendAmount = Satoshis.econ.inGameToBitcoin(amount);

		SendRequest request = SendRequest.to(((BitcoinGenericAddress)tx).getAddress(), sendAmount);
		request.feePerKb = minBitFeeBI;

		localPeerGroup.broadcastTransaction(request.tx);

		try {
			localWallet.commitTx(request.tx);
		} catch (VerificationException e) {
			e.printStackTrace();
			return false;
		}
		saveWallet();

		Satoshis.log.log(Level.WARNING, "Sent transaction: {0}", request.tx.getHash());
		return true;
	}
	
	public boolean sendCoinsToMulti(Map<GenericAddress<?>, Double> toSend) {
		Transaction tx = new Transaction(Satoshis.network);
		double totalSend = 0.0;

		for (Entry<GenericAddress<?>, Double> current : toSend.entrySet()) {
			totalSend += current.getValue() / Satoshis.mult;
			tx.addOutput(Satoshis.econ.inGameToBitcoin(current.getValue()),
					((BitcoinGenericAddress)current.getKey()).getAddress());
		}

		if (totalSend < 0.01) {
			return false;
		}

		SendRequest request = SendRequest.forTx(tx);

		localPeerGroup.broadcastTransaction(request.tx);
		try {
			localWallet.commitTx(request.tx);
		} catch (VerificationException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void refreshWallet() {
		localPeerGroup.stop();
		localWallet.clearTransactions(0);
		new File("plugins/Satoshis/spv.blockchain").delete();
		localPeerGroup.start();
		localPeerGroup.downloadBlockChain();
	}

	public GenericAddress<Address> generateAddress() {
		ECKey key = new ECKey();
		localWallet.importKey(key);
		return new BitcoinGenericAddress(key.toAddress(Satoshis.network));
	}

	public GenericAddress<?> addressContainer() {
		return new BitcoinGenericAddress();
	}

	public NetworkType getNetworkType() {
		return netType;
	}

	public int getConfirms() {
		return confirms;
	}

	public double getMinimumFee() {
		return minBitFee;
	}

	public double getWalletBalance() {
		return localWallet.getBalance().getValue();
	}
}
