import org.bitcoinj.wallet.Wallet;

import java.io.File;

public class DumpWallet {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java DumpWallet <filename>");
            return;
        }

        Wallet wallet = Wallet.loadFromFile(new File(args[0]));
        System.out.println(wallet.toString());
    }
}