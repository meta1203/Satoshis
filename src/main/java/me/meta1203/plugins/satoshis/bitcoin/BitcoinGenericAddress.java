package me.meta1203.plugins.satoshis.bitcoin;

import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import me.meta1203.plugins.satoshis.cryptocoins.GenericAddress;
import me.meta1203.plugins.satoshis.cryptocoins.NetworkType;

public class BitcoinGenericAddress implements GenericAddress<Address> {
	private Address addr;
	
	public BitcoinGenericAddress() {
		this.addr = null;
	}
	
	public BitcoinGenericAddress(Address addr) {
		this.addr = addr;
	}
	
	public Address getAddress() {
		return addr;
	}

	public String getAddressAsString() {
		return addr.toBase58();
	}

	public String getUserReadableInformation() {
		return null;
	}

	public void setAddress(Address addr) {
		this.addr = addr;
	}

	public void setAddress(String addrStr, NetworkType netType) {
		if (netType == NetworkType.PRODUCTION) {
			this.addr = Address.fromBase58(MainNetParams.get(), addrStr);
		} else if (netType == NetworkType.TEST) {
			this.addr = Address.fromBase58(TestNet3Params.get(), addrStr);
		} else if (netType == NetworkType.REGRESSION_TEST) {
			throw new RuntimeException("Regression test is not supported.");
		} else {
			throw new NullPointerException();
		}
	}
}
