package me.meta1203.plugins.satoshis.commands;

import static me.meta1203.plugins.satoshis.commands.CommandUtil.*;
import me.meta1203.plugins.satoshis.Satoshis;
import me.meta1203.plugins.satoshis.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
            String[] arg3) {

        if (!arg0.hasPermission("satoshis.money")) {
            error("You do not have permission for this command!", arg0);
            return true;
        }

        if (arg0 instanceof Player) {
            if (arg3.length == 1) {
                if (Util.testAccount(arg0.getName())) {
                    error("Player not found!", arg0);
                    return true;
                }
                String amount = Satoshis.econ.formatValue(Util.loadAccount(arg0.getName()).getAmount(), true);
                info(arg3[0] + " has " + amount, arg0);
            } else {
                String amount = Satoshis.econ.listMoney(((Player) arg0).getName());
                info("You have " + amount, arg0);
                return true;
            }
        } else {
            if (arg3.length == 1) {
                String amount = Satoshis.econ.listMoney(arg3[0]);
                if (amount == null) {
                    error("Player not found!", arg0);
                    return true;
                }
                info(arg3[0] + " has " + amount, arg0);
            } else {
                error("Syntax: money <player>", arg0);
            }
        }
        return true;
    }

}
