package dogecoin.mc.dogecoins;

import dogecoin.mc.dogecoins.database.AccountEntry;
import org.bitcoinj.core.Coin;

import java.util.UUID;

public class DogecoinsEconAPI {

    public boolean isBuyerResponsible = true;

    public void setFunds(UUID playerUuid, double value) {
        AccountEntry e = Util.loadAccount(playerUuid);
        e.setBalance((int) value);
        Util.saveAccount(e);
    }

    public void addFunds(UUID playerUuid, double value) {
        AccountEntry e = Util.loadAccount(playerUuid);
        e.setBalance(e.getBalance() + (int) value);
        Util.saveAccount(e);
    }

    public void subFunds(UUID playerUuid, double value) {
        AccountEntry e = Util.loadAccount(playerUuid);
        double fVal = e.getBalance() - (int) value;
        if (fVal < 0) {
            fVal = 0;
        }
        e.setBalance(fVal);
        Util.saveAccount(e);
    }

    public double priceOfTax(double traded) {
        return traded * (Dogecoins.tax / 100);
    }

    public void transact(UUID playerFrom, UUID playerTo, double value) {
        if (Dogecoins.salesTax) {
            double tax = priceOfTax(value);
            if (Dogecoins.isBuyerResponsible) {
                subFunds(playerFrom, value + tax);
                addFunds(playerTo, value);
            } else {
                subFunds(playerFrom, value);
                addFunds(playerTo, value - tax);
            }
            addFunds(Dogecoins.owner, tax);
        } else {
            subFunds(playerFrom, value);
            addFunds(playerTo, value);
        }

        Dogecoins.log.info("Transaction took place!");
        Dogecoins.log.info(playerFrom + " paid " + playerTo + ": " + formatValue(value, true));
    }

    public void transferTax(double value) {
        addFunds(Dogecoins.owner, priceOfTax(value));
    }

    public String formatValue(double value, boolean exact) {
        if (exact) {
            value = Util.roundTo(value, 2);
        }
        return value + " " + Dogecoins.currencyName;
    }

    public String listMoney(UUID playerUuid) {
        if (Util.loadAccount(playerUuid) == null) {
            return null;
        }
        return formatValue(Util.loadAccount(playerUuid).getBalance(), true);
    }

    public boolean addAccount(UUID playerUuid) {
        if (Util.testAccount(playerUuid)) {
            return false;
        } else {
            Util.saveAccount(Util.loadAccount(playerUuid));
            return true;
        }
    }

    public double getMoney(UUID playerUuid) {
        return Util.loadAccount(playerUuid).getBalance();
    }

    public boolean hasMoney(UUID playerUuid, double amount) {
        double has = Util.loadAccount(playerUuid).getBalance();
        return has >= amount;
    }

    /**
     * Doge->In-game Doge is 1:1 ratio
     */

    public Coin inGameToDogecoin(double amount) {
        return Coin.valueOf((long) (amount * Math.pow(10, 8)));
//        return Coin.valueOf((long) (amount * Math.pow(10, 8) / Dogecoins.multiplier));
    }

    public double dogecoinToInGame(Coin amount) {
        return (amount.longValue() / Math.pow(10, 8));
//        return (amount.longValue() / Math.pow(10, 8)) * Dogecoins.multiplier;
    }

}
