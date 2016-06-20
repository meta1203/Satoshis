package dogecoin.mc.dogecoins.commands;

import dogecoin.mc.dogecoins.Dogecoins;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CheckCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("dogecoins.info")) {
            sender.sendMessage(Dogecoins.scanner.getInfo());
        } else {
            CommandUtil.error("You do not have permission for this command!", sender);
        }
        return true;
    }

}
