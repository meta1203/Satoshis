package me.meta1203.plugins.satoshis;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "ss_accounts")
public class AccountEntry {
	@Id
    private int id;
	@NotNull
    private String playerName;
	@Column
	private double amount; 
	@Column
	private String addr;
	
	// ID
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	// Player Name
	
	public void setPlayerName(String name) {
		playerName = name;
	}
	public String getPlayerName() {
		return playerName;
	}
	
	// Amount
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getAmount() {
		return this.amount;
	}
	
	// Bitcoin Address
	
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
}
