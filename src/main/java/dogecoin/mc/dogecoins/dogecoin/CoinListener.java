package dogecoin.mc.dogecoins.dogecoin;
import com.google.common.util.concurrent.Futures;
import dogecoin.mc.dogecoins.Dogecoins;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.util.ArrayList;
import java.util.List;

public class CoinListener implements WalletCoinsReceivedEventListener {

    public static List<Transaction> pending = new ArrayList<>();

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        System.out.println("onCoinsReceived");
        pending.add(tx);
        Futures.addCallback(tx.getConfidence().getDepthFuture(Dogecoins.confirms), new TransactionListener());
    }

}
