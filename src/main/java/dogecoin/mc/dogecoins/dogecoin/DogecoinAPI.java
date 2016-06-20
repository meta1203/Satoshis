package dogecoin.mc.dogecoins.dogecoin;

import dogecoin.mc.dogecoins.Dogecoins;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DogecoinAPI {

    // 1 DOGE per KB
    public static final Coin minDogeFee = Coin.valueOf(1);
    public static final double minDogeFeeRaw = minDogeFee.longValue();

    private final File walletFile;
    private Wallet wallet;
    private SPVBlockStore blockStore;
    private BlockChain blockChain;
    private PeerGroup peerGroup = null;

    public DogecoinAPI() {
        walletFile = new File("plugins/Dogecoins/wallet.wallet");
        try {
            wallet = Wallet.loadFromFile(walletFile);
        } catch (UnreadableWalletException e) {
            wallet = new Wallet(Dogecoins.network);
        }
        try {
            blockStore = new SPVBlockStore(Dogecoins.network, new File("plugins/Dogecoins/doge.blockchain"));
            blockChain = new BlockChain(Dogecoins.network, wallet, blockStore);
        } catch (BlockStoreException ex) {
            ex.printStackTrace();
        }
        wallet.addCoinsReceivedEventListener(new CoinListener());
        wallet.autosaveToFile(walletFile, 30, TimeUnit.SECONDS, null);

        peerGroup = new PeerGroup(Dogecoins.network, blockChain);
        peerGroup.addPeerDiscovery(new DnsDiscovery(Dogecoins.network));
        peerGroup.setUserAgent("DogecoinsBukkit", "1.0.1");
        peerGroup.addWallet(wallet);

        Dogecoins.log.info("Connecting to peers...");
        peerGroup.start();

        Dogecoins.log.info("Downloading Blockchain...");
        final boolean[] downloaded = {false};
        peerGroup.startBlockChainDownload(new DownloadProgressTracker() {
            private long startTime;
            private int totalBlocks;

            @Override
            protected void startDownload(int blocks) {
                totalBlocks = blocks;
                startTime = System.currentTimeMillis();
                System.out.println("Downloading starting");
            }

            @Override
            protected void progress(double pct, int blocksSoFar, Date date) {
                System.out.println("Blockchain percentage: " + (int) pct + "% | " + (totalBlocks - blocksSoFar) + " out of " + totalBlocks + ".");
                long duration = System.currentTimeMillis() - startTime;
                long etaTime = (long) ((100 - pct) * (duration / pct));

                long second = (etaTime / 1000) % 60;
                long minute = (etaTime / (1000 * 60)) % 60;
                long hour = (etaTime / (1000 * 60 * 60)) % 24;

                System.out.println(String.format("%02d:%02d:%02d remaining", hour, minute, second));
            }

            @Override
            protected void doneDownload() {
                downloaded[0] = true;
                System.out.println("Download complete");

                // Debugging
                Dogecoins.log.info("Wallet: " + wallet);
                Dogecoins.log.info("Wallet balance: " + wallet.getBalance());
                Dogecoins.log.info("Wallet imported keys: " + Arrays.toString(wallet.getImportedKeys().toArray()));
                Dogecoins.log.info("Wallet receive keys: " + Arrays.toString(wallet.getIssuedReceiveAddresses().toArray()));
            }
        });
        while (!downloaded[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Dogecoins.log.info("Done, loading the rest of the plugin...");
    }

    public Address generateAddress() {
        ECKey key = new ECKey();
        if (wallet.importKey(key)) {
            Dogecoins.log.info("Successfully imported new key " + key.toAddress(Dogecoins.network) + ".");
        } else {
            Dogecoins.log.info("Key already exists: " + key.toAddress(Dogecoins.network) + ".");
        }
        Dogecoins.log.info("Wallet: " + wallet);
        return key.toAddress(Dogecoins.network);
    }

    @Override
    protected void finalize() throws Throwable {
        wallet.saveToFile(walletFile);
    }

    public boolean localSendCoins(Address address, double value) {
        Coin sendAmount = Dogecoins.econ.inGameToDogecoin(value);

        SendRequest request = SendRequest.to(address, sendAmount);
        request.feePerKb = minDogeFee;

        peerGroup.broadcastTransaction(request.tx);

        try {
            wallet.commitTx(request.tx);
        } catch (VerificationException e) {
            e.printStackTrace();
        }
        saveWallet();

        Dogecoins.log.log(Level.WARNING, "Sent transaction: {0}", request.tx.getHash());
        return true;
    }

    public boolean sendCoinsMulti(Map<Address, Double> toSend) {
        Transaction transaction = new Transaction(Dogecoins.network);
        double totalSend = 0.0;

        for (Entry<Address, Double> current : toSend.entrySet()) {
//            totalSend += current.getValue() / Dogecoins.multiplier;
            totalSend += current.getValue();
            transaction.addOutput(Dogecoins.econ.inGameToDogecoin(current.getValue()),
                    current.getKey());
        }

        if (totalSend < 0.01) {
            return false;
        }

        SendRequest request = SendRequest.forTx(transaction);

        peerGroup.broadcastTransaction(request.tx);
        try {
            wallet.commitTx(request.tx);
        } catch (VerificationException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void saveWallet() {
        try {
            wallet.saveToFile(walletFile);
            peerGroup.stop();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public Wallet getWallet() {
        return wallet;
    }

    public BlockChain getChain() {
        return blockChain;
    }

    public void reloadWallet() {
        peerGroup.stop();
        wallet.clearTransactions(0);
        new File("plugins/Dogecoins/spv.blockchain").delete();
        peerGroup.start();
        peerGroup.downloadBlockChain();
    }

}
