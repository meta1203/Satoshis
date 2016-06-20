package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Dogecoins;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.math.BigInteger;

public class AdminCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dogecoins.admin")) {
            CommandUtil.error("You do not have permission for this command!", sender);
            return true;
        }

        if (args.length != 1) {
            CommandUtil.error("Syntax: /dogecoins <info>|<reset>", sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("info")) {
            printInfo(sender);
        } else if (args[0].equalsIgnoreCase("reset")) {
            Dogecoins.dogecoinAPI.reloadWallet();
        } else {
            CommandUtil.error("Syntax: /dogecoins <info>|<reset>", sender);
        }

        return true;
    }

    private void printInfo(CommandSender arg0) {
        CommandUtil.info("INFO:", arg0);
        CommandUtil.info("Wallet:", arg0);

        Wallet tmp = Dogecoins.dogecoinAPI.getWallet();
        Coin dogecoinBalance = tmp.getBalance();
        double inGameValue = Dogecoins.econ.dogecoinToInGame(dogecoinBalance);
        CommandUtil.info("Total balance: " + dogecoinBalance.longValue() + " mDoge = " + Dogecoins.econ.formatValue(inGameValue, true), arg0);
        CommandUtil.info("Recent transactions:", arg0);
        for (Transaction t : tmp.getRecentTransactions(3, false)) {
            try {
                CommandUtil.info(t.getHashAsString() + " value: +" + t.getValueSentToMe(tmp) + ", -" + t.getValueSentFromMe(tmp), arg0);
                CommandUtil.info("Confirmations: " + t.getConfidence().getDepthInBlocks(), arg0);
            } catch (ScriptException e) {
                CommandUtil.error("Transaction " + t.getHashAsString() + " errored out!", arg0);
            } catch (IllegalStateException e) {
                continue;
            }
        }
    }
}
