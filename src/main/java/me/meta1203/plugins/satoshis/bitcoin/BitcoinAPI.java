package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.meta1203.plugins.satoshis.Satoshis;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.BoundedOverheadBlockStore;

public class BitcoinAPI {
	
	public Wallet wallet;
	public BlockStore block;
	BlockChain chain;
	final PeerGroup peerGroup;
	public static Map<Address, String> allocatedAddresses = new HashMap<Address, String>();
	public static List<Address> unallocatedAddresses = new ArrayList<Address>();
	
	public BitcoinAPI() {
		final File walletFile = new File(Satoshis.walletFile);
		try {
		    wallet = Wallet.loadFromFile(walletFile);
		    for (ECKey current : wallet.getKeys()) {
				unallocatedAddresses.add(current.toAddress(NetworkParameters.prodNet()));
			}
		} catch (IOException e) {
		    wallet = new Wallet(NetworkParameters.prodNet());
		    addAddressesToWallet(5);
		    try {
				wallet.saveToFile(walletFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			block = new BoundedOverheadBlockStore(NetworkParameters.prodNet(), new File("plugins/Satoshis/store.blockchain"));
			chain = new BlockChain(NetworkParameters.prodNet(), wallet, block);
		} catch (BlockStoreException e) {
			e.printStackTrace();
		}
		peerGroup = new PeerGroup(NetworkParameters.prodNet(), chain);
		peerGroup.setUserAgent("SatoshisBukkit", "0.1");
		peerGroup.addWallet(wallet);
		peerGroup.addPeerDiscovery(new DnsDiscovery(NetworkParameters.prodNet()));
		peerGroup.start();
		peerGroup.downloadBlockChain();
	}
	
	private void addAddressesToWallet(int num) {
		for (int x = 0; x < num; x++) {
			ECKey key = new ECKey();
			wallet.addKey(key);
			unallocatedAddresses.add(key.toAddress(NetworkParameters.prodNet()));
		}
	}
	
	public void allocate(Address a, String name) {
		allocatedAddresses.put(a, name);
		unallocatedAddresses.remove(a);
	}
	
	public Address allocate(String name) {
		if (unallocatedAddresses.size() < 1) {
			
		}
		allocatedAddresses.put(unallocatedAddresses.get(0), name);
		Address ret = unallocatedAddresses.get(0);
		unallocatedAddresses.remove(0);
		return ret;
	}
	
	public void deallocate(Address a) {
		allocatedAddresses.remove(a);
		unallocatedAddresses.add(a);
	}

	@Override
	protected void finalize() throws Throwable {
		wallet.saveToFile(new File(Satoshis.walletFile));
	}
	
	public boolean sendCoins(Address a, double value) {
		Transaction tx = wallet.sendCoins(peerGroup, a, BigInteger.valueOf((long)(value * Math.pow(10, 8)/Satoshis.mult)));
		if (tx != null) {
			return true;
		} else {
			return false;
		}
	}
	
}
