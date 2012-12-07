package me.meta1203.plugins.satoshis.bitcoin;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.meta1203.plugins.satoshis.Satoshis;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerAddress;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Wallet;
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
		} catch (IOException e) {
		    wallet = new Wallet(NetworkParameters.prodNet());
		    wallet.addKey(new ECKey());
		    addAddressesToWallet(5);
		    try {
				wallet.saveToFile(walletFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			block = new BoundedOverheadBlockStore(NetworkParameters.prodNet(), new File("plugins/Satoshis/block.blockchain"));
			chain = new BlockChain(NetworkParameters.prodNet(), wallet, block);
		} catch (BlockStoreException e) {
			e.printStackTrace();
		}
		peerGroup = new PeerGroup(NetworkParameters.prodNet(), chain);
		peerGroup.setUserAgent("SatoshisBukkit", "0.1");
		peerGroup.addWallet(wallet);
		try {
			peerGroup.addAddress(new PeerAddress(InetAddress.getLocalHost()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		peerGroup.start();
		for (ECKey current : wallet.getKeys()) {
			unallocatedAddresses.add(current.toAddress(NetworkParameters.prodNet()));
		}
	}
	
	private void addAddressesToWallet(int num) {
		for (int x = 0; x < num; x++) {
			wallet.addKey(new ECKey());
		}
	}
	
	public void allocate(Address a, String name) {
		allocatedAddresses.put(a, name);
		unallocatedAddresses.remove(a);
	}
	
	public void deallocate(Address a) {
		allocatedAddresses.remove(a);
		unallocatedAddresses.add(a);
	}
	
}
