package dogecoin.mc.dogecoins.database;

import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.Util;
import org.bukkit.ChatColor;

public class DatabaseScanner {

    private Dogecoins plugin;

    public DatabaseScanner(Dogecoins plugin) {
        this.plugin = plugin;
    }

    private double getTotal() {
        double ret = 0;
        for (AccountEntry curr : AccountDatabase.getAccounts()) {
            ret += curr.getBalance();
        }
        return ret;
    }

    public DatabaseLevel getLevel(double valueGame) {
        double valueReal = Util.getDogecoin(Dogecoins.dogecoinAPI.getWallet().getBalance());
//        valueGame = valueGame / Dogecoins.multiplier;

        if (valueReal > valueGame) {
            return DatabaseLevel.UNDER;
        } else if (valueReal == valueGame) {
            return DatabaseLevel.PERFECT;
        } else if (valueGame - valueReal <= 1) {
            return DatabaseLevel.WARNING;
        } else {
            return DatabaseLevel.SEVERE;
        }
    }

    public double getOffset(double valueGame) {
        double valueReal = Util.getDogecoin(Dogecoins.dogecoinAPI.getWallet().getBalance());
//        valueGame = valueGame / Dogecoins.multiplier;
        return valueGame - valueReal;
    }

    public String getInfo() {
        double scanned = getTotal();
        DatabaseLevel level = getLevel(scanned);
        String info = level.getColor() + "";
        switch (level) {
            case PERFECT:
                info += "System is healthy!";
                break;
            case SEVERE:
                info += "SYSTEM SEVERELY OVERDRAWN!\n"
                        + "Over by " + getOffset(scanned) + " DOGE!\n"
                        + "Total economy reset is recommended!";
                break;
            case UNDER:
                info += "More Dogecoin than " + Dogecoins.currencyName + " exists!\n";
                break;
            case WARNING:
                info += "System overdrawn!\n"
                        + "Over by " + getOffset(scanned) + " DOGE.\n"
                        + "It is recommended to equalize funding by adding DOGE directly.";
                break;
            default:
                break;
        }

        // Don't forget to turn the color off
        info += ChatColor.RESET;

        return info;
    }
}
