package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Util;
import org.bitcoinj.core.Address;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DepositCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("dogecoins.deposit")) {
            CommandUtil.error("You do not have permission for this command!", sender);
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Address alloc = Util.parseAddress(Util.loadAccount(player.getUniqueId()).getDogeAddress());
            CommandUtil.info("The following link contains your address information:", sender);
            CommandUtil.info("https://dogechain.info/address/" + (alloc != null ? alloc.toString() : "NULL ADDRESS REPORT ERROR TO DEV"), sender);
            CommandUtil.info("This is your permanent address. Send your Dogecoins here to receive them in-game.", sender);
        }
        return true;
    }

}
