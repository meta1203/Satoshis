package dogecoin.mc.dogecoins;

import dogecoin.mc.dogecoins.database.AccountDatabase;
import dogecoin.mc.dogecoins.database.AccountEntry;
import org.bitcoinj.core.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Util {

    public static final Logger log = Logger.getLogger("Minecraft");

    public static double roundTo(double input, int place) {
        return Math.round(input * Math.pow(10, place)) / Math.pow(10, place);
    }

    public static double getDogecoin(Coin raw) {
        return raw.longValue() / Math.pow(10, 8);
    }

    public static boolean testAccount(UUID playerUuid) {
        AccountEntry ae = AccountDatabase.getAccount(playerUuid);
        return ae != null;
    }

    public static AccountEntry loadAccount(UUID playerUuid) {
        log.fine("Loading account for " + playerUuid.toString());
        AccountEntry ae = AccountDatabase.getAccount(playerUuid);
        if (ae == null) {
            log.fine("No account exists for " + playerUuid.toString() + ". Creating a new account.");
            ae = new AccountEntry(playerUuid, 0, Dogecoins.dogecoinAPI.generateAddress().toString());
            saveAccount(ae);
        } else if (ae.getDogeAddress() == null) {
            log.fine("Account found for " + playerUuid.toString() + ", but it does not contain an address. Creating a new account.");
            ae.setDogeAddress(Dogecoins.dogecoinAPI.generateAddress().toString());
            saveAccount(ae);
        }
        return ae;
    }

    public static void saveAccount(AccountEntry accountEntry) {
        System.out.println("Attempting to save Account Entry: " + accountEntry.toString());
        AccountDatabase.save(accountEntry);
    }

    public static UUID searchAddress(Address address) {
        AccountEntry ae = null;
        for (AccountEntry curr : AccountDatabase.getAccounts()) {
            if (curr.getDogeAddress().equals(address.toString())) {
                ae = curr;
            }//found the account
        }        //The account should exist at this point, but we can check. If not, we will send the money to the owner of the system.
        if (ae == null) {
            return Dogecoins.owner;
        }
        return ae.getPlayerUuid();
    }

    public static void serializeChecking(List<Transaction> toSerialize) {
        File save = new File("plugins/Dogecoins/tx.temp");
        PrintWriter pw;
        try {
            pw = new PrintWriter(save);
            for (Transaction current : toSerialize) {
                pw.println(current.getHash().toString());
            }
            pw.flush();
            pw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Address parseAddress(String address) {
        try {
            return new Address(Dogecoins.network, address);
        } catch (WrongNetworkException e) {
            e.printStackTrace();
            return null;
        } catch (AddressFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Transaction> loadChecking() {
        File open = new File("plugins/Dogecoins/tx.temp");
        List<Transaction> ret = new ArrayList<>();
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(open));
        } catch (FileNotFoundException e) {
            return ret;
        }
        String strLine;
        try {
            while ((strLine = in.readLine()) != null) {
                ret.add(new Transaction(Dogecoins.network, Sha256Hash.wrap(strLine).getBytes()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
            open.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static List<Address> getContainedAddress(List<TransactionOutput> tx) throws ScriptException {
        List<Address> ret = new ArrayList<>();
        for (TransactionOutput current : tx) {
            if (current.isMine(Dogecoins.dogecoinAPI.getWallet())) {
                ret.add(current.getScriptPubKey().getToAddress(Dogecoins.network));
            }
        }
        return ret;
    }

}
