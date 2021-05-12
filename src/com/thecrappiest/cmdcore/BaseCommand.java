package com.thecrappiest.cmdcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.thecrappiest.cmdcore.methods.MessageUtil;

public class BaseCommand implements CommandExecutor {

	CommandCore cc = CommandCore.getCMDCore();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("commandcore")) {
			if (sender.hasPermission("CommandCore.reload")) {
				if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
					cc.reloadConfig();

					CommandsHolder ch = CommandsHolder.retrieveCommandsHolder();
					ch.aliases.clear();
					ch.customCommands.clear();
					ch.addConfiguredAliases();
					ch.addConfiguredCommands();

					sender.sendMessage(MessageUtil.utils().color("&aCommandCore config has been reloaded."));
				}
			}
			return true;
		}
		return false;
	}

}
