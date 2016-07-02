package me.meta1203.plugins.satoshis.commands;

import static me.meta1203.plugins.satoshis.commands.CommandUtil.error;
import static me.meta1203.plugins.satoshis.commands.CommandUtil.info;

import org.bitcoinj.core.Address;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.meta1203.plugins.satoshis.Util;

public class DepositCommand implements CommandExecutor {

    public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
            String[] arg3) {
        if (!arg0.hasPermission("satoshis.deposit")) {
            error("You do not have permission for this command!", arg0);
            return true;
        }

        if (arg0 instanceof Player) {
            Player player = (Player) arg0;
            String name = player.getName();
            Address alloc = Util.parseAddress(Util.loadAccount(name).getAddr());
            info("The following link contains your address information:", arg0);
            info("www.btc.to/" + alloc.toString() + "/be", arg0);
            // info(alloc.toString(), arg0);
            info("This address is yours forever. \nAdd it to your address book if need-be.", arg0);
        }
        return true;
    }

}
