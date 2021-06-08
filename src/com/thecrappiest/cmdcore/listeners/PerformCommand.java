package com.thecrappiest.cmdcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.thecrappiest.cmdcore.CommandCore;
import com.thecrappiest.cmdcore.CommandsHolder;
import com.thecrappiest.cmdcore.methods.Actions;
import com.thecrappiest.cmdcore.methods.MessageUtil;
import com.thecrappiest.cmdcore.objects.CustomizedCommand;

public class PerformCommand implements Listener {

	public final CommandCore cc;

	public PerformCommand(CommandCore cc) {
		this.cc = cc;
		Bukkit.getPluginManager().registerEvents(this, cc);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String command = event.getMessage();
		MessageUtil msgUtil = MessageUtil.utils();

		String base = null;
		if (command.split(" ").length > 0) {
			base = command.split(" ")[0].replace("/", "");
		} else {
			base = command.replace("/", "");
		}
		base = base.toLowerCase();

		String[] args = null;
		String arguments = command.replace("/", "").replace(base + " ", "").replace(base, "");
		if (arguments.length() > 0) {
			args = arguments.split(" ");
		}

		CommandsHolder ch = CommandsHolder.retrieveCommandsHolder();
		String alias = null;
		if (ch.aliases.containsKey(base)) {
			alias = ch.aliases.get(base);
			event.setCancelled(true);
			if (!ch.customCommands.containsKey(alias)) {
				p.performCommand(command.replace(base, alias));
				return;
			}
		}
		
		CustomizedCommand custom = alias == null ? ch.customCommands.get(base) : ch.customCommands.get(alias);
		if (custom != null) {
			event.setCancelled(true);

			if (custom.getPermission() != null && !p.hasPermission(custom.getPermission())) {
				if (custom.getNoPermMessage() != null)
					p.sendMessage(msgUtil.color(msgUtil.addPlaceHolders(p, custom.getNoPermMessage())));
				return;
			}

			if (custom.getCost() > 0) {
				if (!cc.hasAmount(Bukkit.getOfflinePlayer(p.getUniqueId()), custom.getCost())) {
					if (custom.getNoPermMessage() != null)
						p.sendMessage(msgUtil.color(msgUtil.addPlaceHolders(p, custom.retrieveInvalidFundsMessage())));
					return;
				} else {
					cc.economy.withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), custom.getCost());
				}
			}
			
			if (!custom.retrieveActions().isEmpty()) {
				String[] finalArgs = args;
				custom.retrieveActions().forEach(action -> {
					Actions.runAction(p, action, finalArgs);
				});
			}
		}
	}

}
