package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.bitcoin.core.*;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.BoundedOverheadBlockStore;
import me.meta1203.plugins.satoshis.Satoshis;

//import com.google.bitcoin.*;
//import com.google.bitcoin.core.Address;
//import com.google.bitcoin.core.BlockChain;
//import com.google.bitcoin.core.ECKey;
//import com.google.bitcoin.core.NetworkParameters;
//import com.google.bitcoin.core.PeerGroup;
//import com.google.bitcoin.core.Transaction;
//import com.google.bitcoin.core.VerificationException;
//import com.google.bitcoin.core.Wallet;
//import com.google.bitcoin.discovery.DnsDiscovery;
//import com.google.bitcoin.store.BlockStore;
//import com.google.bitcoin.store.BlockStoreException;
//import com.google.bitcoin.store.BoundedOverheadBlockStore;

public class BitcoinAPI {

	private Wallet localWallet;
    private BlockStore localBlock;
    private BlockChain localChain;
	private final File walletFile;
    private PeerGroup localPeerGroup = null;
    public static Map<Address, String> allocatedAddresses = new HashMap<Address, String>();
    public static List<Address> unallocatedAddresses = new ArrayList<Address>();
	
	public BitcoinAPI() {
		walletFile = new File("plugins/Satoshis/wallet.wallet");
		try {
		    localWallet = Wallet.loadFromFile(walletFile);
		    Satoshis.log.info(localWallet.toString());
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
            localBlock = new BoundedOverheadBlockStore(NetworkParameters.prodNet(), new File("plugins/Satoshis/store.blockchain"));
            localChain = new BlockChain(NetworkParameters.prodNet(), localWallet, localBlock);
		} catch (BlockStoreException ex) {
			ex.printStackTrace();
		}
        localWallet.addEventListener(new CoinListener());
        localPeerGroup = new PeerGroup(NetworkParameters.prodNet(), localChain);
        localPeerGroup.setUserAgent("SatoshisBukkit", "0.1");
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
        BigInteger sendAmount = BigInteger.valueOf((long)(value * Math.pow(10, 8)/Satoshis.mult));

        Wallet.SendResult sr = null;
        sr = localWallet.sendCoins(localPeerGroup, a, sendAmount);
        if (sr == null)
            return false;
        else
            return  true;


        // FROM Wallet.java:
        //     * <p>If you just want to send money quickly, you probably want
        //     * {@link Wallet#sendCoins(PeerGroup, Address, java.math.BigInteger)} instead. That will create the sending
        //     * transaction, commit to the wallet and broadcast it to the network all in one go. This method is lower level
        //    * and lets you see the proposed transaction before anything is done with it.</p>
        /*
        if (tx != null) {
			Satoshis.log.warning("Sent " + (value * Math.pow(10, 8)/Satoshis.mult) + " BTC in transaction: " + tx.getHashAsString());
			try {
                localWallet.commitTx(tx);
			} catch (VerificationException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
		*/
	}
	
	private boolean testAllocated(String name) {
		return allocatedAddresses.containsValue(name);
	}
	
	public void saveWallet() {
		try {
            localWallet.saveToFile(walletFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

    public Wallet getWallet(){
        return localWallet;
    }

    public BlockChain getChain(){
        return localChain;
    }
}
