package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public Map<Address, String> allocatedAddresses = new HashMap<Address, String>();
    public List<Address> unallocatedAddresses = new ArrayList<Address>();
    public final BigInteger minBitFee = BigInteger.valueOf((long)(0.0005*Math.pow(10, 8)));
	
	public BitcoinAPI() {
		walletFile = new File("plugins/Satoshis/wallet.wallet");
		try {
		    localWallet = Wallet.loadFromFile(walletFile);
		    // Satoshis.log.info(localWallet.toString());
		    for (ECKey current : localWallet.getKeys()) {
				unallocatedAddresses.add(current.toAddress(NetworkParameters.prodNet()));
			}
		} catch (IOException e) {
            localWallet = new Wallet(NetworkParameters.prodNet());
		    addAddressesToWallet(5);
		    try {
                localWallet.saveToFile(walletFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
            localBlock = new SPVBlockStore(NetworkParameters.prodNet(), new File("plugins/Satoshis/spv.blockchain"));
            localChain = new BlockChain(NetworkParameters.prodNet(), localWallet, localBlock);
		} catch (BlockStoreException ex) {
			ex.printStackTrace();
		}
        localWallet.addEventListener(new CoinListener());
        localPeerGroup = new PeerGroup(NetworkParameters.prodNet(), localChain);
        localPeerGroup.setUserAgent("SatoshisBukkit", "0.2");
        localPeerGroup.addWallet(localWallet);
        localPeerGroup.addPeerDiscovery(new DnsDiscovery(NetworkParameters.prodNet()));
        localPeerGroup.start();
        localPeerGroup.downloadBlockChain();
	}

	private void addAddressesToWallet(int num) {
		for (int x = 0; x < num; x++) {
			ECKey key = new ECKey();
            localWallet.addKey(key);
			unallocatedAddresses.add(key.toAddress(NetworkParameters.prodNet()));
		}
	}
	
	public void allocate(Address a, String name) {
		allocatedAddresses.put(a, name);
		unallocatedAddresses.remove(a);
	}
	
	public Address allocate(String name) {
		Address ret = null;
		if (unallocatedAddresses.size() < 1) {
			addAddressesToWallet(1);
		}
		if (testAllocated(name)) {
			for (Map.Entry<Address, String> current : allocatedAddresses.entrySet()) {
				if (current.getValue().equals(name)) {
					ret = current.getKey();
					break;
				}
			}
		} else {
			ret = unallocatedAddresses.get(0);
			allocatedAddresses.put(ret, name);
			unallocatedAddresses.remove(0);
		}
		return ret;
	}
	
	public void deallocate(Address a) {
		allocatedAddresses.remove(a);
		unallocatedAddresses.add(a);
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
		Transaction tx = new Transaction(NetworkParameters.prodNet());
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
	
	private boolean testAllocated(String name) {
		return allocatedAddresses.containsValue(name);
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
    	new File("plugins/Satoshis/store.blockchain").delete();
    	localPeerGroup.start();
    	localPeerGroup.downloadBlockChain();
    }
}
