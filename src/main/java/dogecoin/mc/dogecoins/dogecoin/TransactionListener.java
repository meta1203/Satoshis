package dogecoin.mc.dogecoins.dogecoin;


import com.google.common.util.concurrent.FutureCallback;
import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.Util;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.TransactionConfidence;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class TransactionListener implements FutureCallback<TransactionConfidence> {

    public void onSuccess(TransactionConfidence transaction) {
        parseTransaction(transaction);
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }

    public void parseTransaction(TransactionConfidence tx) {
        Dogecoins.log.log(Level.WARNING, "Transaction with id {0} has reached {1} confirmations. Adding to player...",
                new Object[]{tx.getOverridingTransaction().getHashAsString(), Dogecoins.confirms});
        try {
            double value = Dogecoins.econ.dogecoinToInGame(tx.getOverridingTransaction().getValueSentToMe(Dogecoins.dogecoinAPI.getWallet()));
            List<Address> receivers = Util.getContainedAddress(tx.getOverridingTransaction().getOutputs());

            for (Address x : receivers) {
                UUID playerUuid = Util.searchAddress(x);
                Dogecoins.econ.addFunds(playerUuid, value);
                Dogecoins.log.warning("Added " + Dogecoins.econ.formatValue(value, true) + " to " + playerUuid + "!");
            }

            Dogecoins.dogecoinAPI.saveWallet();
            CoinListener.pending.remove(tx.getOverridingTransaction());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

}
