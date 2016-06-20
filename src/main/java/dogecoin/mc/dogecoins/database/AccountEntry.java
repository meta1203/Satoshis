package dogecoin.mc.dogecoins.database;

import java.util.UUID;

public class AccountEntry {

    private UUID playerUuid;
    private double balance;
    private String dogeAddress;

    public AccountEntry(UUID playerUuid, double balance, String dogeAddress) {
        this.playerUuid = playerUuid;
        this.balance = balance;
        this.dogeAddress = dogeAddress;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getDogeAddress() {
        return dogeAddress;
    }

    public void setDogeAddress(String dogeAddress) {
        this.dogeAddress = dogeAddress;
    }

    @Override
    public String toString() {
        return "AccountEntry{" +
                "playerUuid=" + playerUuid +
                ", balance=" + balance +
                ", dogeAddress='" + dogeAddress + '\'' +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new AccountEntry(UUID.fromString(playerUuid.toString()), balance, String.valueOf(dogeAddress));
    }

}
