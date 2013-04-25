package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

import com.google.bitcoin.core.*;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.SPVBlockStore;

import me.meta1203.plugins.satoshis.Satoshis;

public class BitcoinAPI {

	private Wallet localWallet;
    private SPVBlockStore localBlock;
    private BlockChain localChain;
	private final File walletFile;
    private PeerGroup localPeerGroup = null;
    public final BigInteger minBitFee = BigInteger.valueOf((long)(0.0005*Math.pow(10, 8)));
	
	public BitcoinAPI() {
		walletFile = new File("plugins/Satoshis/wallet.wallet");
		try {
		    localWallet = Wallet.loadFromFile(walletFile);
		    // Satoshis.log.info(localWallet.toString());
		} catch (IOException e) {
            localWallet = new Wallet(Satoshis.network);
		}
		try {
            localBlock = new SPVBlockStore(Satoshis.network, new File("plugins/Satoshis/spv.blockchain"));
            localChain = new BlockChain(Satoshis.network, localWallet, localBlock);
		} catch (BlockStoreException ex) {
			ex.printStackTrace();
		}
        localWallet.addEventListener(new CoinListener());
        localPeerGroup = new PeerGroup(Satoshis.network, localChain);
        localPeerGroup.setUserAgent("SatoshisBukkit", "0.2");
        localPeerGroup.addWallet(localWallet);
        localPeerGroup.addPeerDiscovery(new DnsDiscovery(Satoshis.network));
        localPeerGroup.start();
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
        request.fee = minBitFee;
        
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
			tx.addOutput(Satoshis.econ.inGameToBitcoin(current.getValue()), current.getKey());
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
