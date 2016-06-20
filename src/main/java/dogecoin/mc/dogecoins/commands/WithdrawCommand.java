package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Dogecoins;
import dogecoin.mc.dogecoins.dogecoin.DogecoinAPI;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.WrongNetworkException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dogecoins.withdraw")) {
            CommandUtil.error("You do not have permission for this command!", sender);
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Withdraw exact amount
            if (args.length == 2) {
                try {
                    Address withdrawTo = Address.fromBase58(Dogecoins.network, args[0]);
                    double withdraw = Double.parseDouble(args[1]);
                    if (!Dogecoins.econ.hasMoney(player.getUniqueId(), Dogecoins.minWithdraw)) {
                        CommandUtil.error("Oops! You must have " + Dogecoins.econ.formatValue(Dogecoins.minWithdraw, false) + " to withdraw!", sender);
                        return true;
                    }
                    if (!Dogecoins.econ.hasMoney(player.getUniqueId(), withdraw)) {
                        CommandUtil.error("Oops! You cannot withdraw more money than you have!", sender);
                        return true;
                    }
                    if (!Dogecoins.salesTax && !player.getUniqueId().equals(Dogecoins.owner)) {
                        Dogecoins.dogecoinAPI.localSendCoins(withdrawTo, withdraw - Dogecoins.econ.priceOfTax(withdraw) - DogecoinAPI.minDogeFeeRaw);
                    } else {
                        Dogecoins.dogecoinAPI.localSendCoins(withdrawTo, withdraw - DogecoinAPI.minDogeFeeRaw);
                    }
                    CommandUtil.action("Sending " + Dogecoins.econ.formatValue(withdraw - DogecoinAPI.minDogeFeeRaw, false) + " to address " + withdrawTo.toString() + " sucessfully!", sender);
                    Dogecoins.econ.subFunds(player.getUniqueId(), withdraw + DogecoinAPI.minDogeFeeRaw);
                } catch (WrongNetworkException e) {
                    CommandUtil.error("Oops! That address was for the TestNet!", sender);
                } catch (AddressFormatException e) {
                    CommandUtil.error("Oops! Is that the correct address?", sender);
                } catch (NumberFormatException e) {
                    CommandUtil.error("Syntax: /withdraw <address> [amount]", sender);
                    CommandUtil.error("Amount must be a number!", sender);
                }
            } else if (args.length == 1) {
                try {
                    Address withdrawTo = Address.fromBase58(Dogecoins.network, args[0]);
                    double withdraw = Dogecoins.econ.getMoney(player.getUniqueId());
                    if (withdraw == 0 + DogecoinAPI.minDogeFeeRaw) {
                        CommandUtil.error("Oops! You have no money in your account!", sender);
                        return true;
                    }
                    if (!Dogecoins.salesTax && !player.getUniqueId().equals(Dogecoins.owner)) {
                        Dogecoins.dogecoinAPI.localSendCoins(withdrawTo, withdraw - Dogecoins.econ.priceOfTax(withdraw) - DogecoinAPI.minDogeFeeRaw);
                    } else {
                        Dogecoins.dogecoinAPI.localSendCoins(withdrawTo, withdraw - DogecoinAPI.minDogeFeeRaw);
                    }
                    CommandUtil.action("Sending " + Dogecoins.econ.formatValue(withdraw - DogecoinAPI.minDogeFeeRaw, false) + " to address " + withdrawTo.toString() + " sucessfully!", sender);

                    Dogecoins.econ.subFunds(player.getUniqueId(), withdraw);
                } catch (WrongNetworkException e) {
                    CommandUtil.error("Oops! That address was for the TestNet!", sender);
                } catch (AddressFormatException e) {
                    CommandUtil.error("Oops! Is that the correct address?", sender);
                } catch (NumberFormatException e) {
                    CommandUtil.error("Syntax: /withdraw <address> [amount]", sender);
                    CommandUtil.error("Amount must be a number!", sender);
                }
            } else {
                CommandUtil.error("Syntax: /withdraw <address> [amount]", sender);
            }
        }

        return true;
    }

}
