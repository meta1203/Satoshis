package me.meta1203.plugins.satoshis.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandUtil {
	public static void error(String msg, CommandSender cs) {
		cs.sendMessage(ChatColor.RED + msg);
	}
	
	public static void info(String msg, CommandSender cs) {
		cs.sendMessage(ChatColor.AQUA + msg);
	}
	
	public static void action(String msg, CommandSender cs) {
		cs.sendMessage(ChatColor.GREEN + msg);
	}
}
