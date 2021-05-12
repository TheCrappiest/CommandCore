package com.thecrappiest.cmdcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.thecrappiest.cmdcore.objects.CustomizedCommand;

public class CommandsHolder {

	public static CommandsHolder instance;

	public static CommandsHolder retrieveCommandsHolder() {
		if (instance == null) {
			instance = new CommandsHolder();
		}
		return instance;
	}

	CommandCore cmdCore = CommandCore.getCMDCore();

	public Map<String, String> aliases = new HashMap<>();
	public Map<String, CustomizedCommand> customCommands = new HashMap<>();

	public void addConfiguredAliases() {
		int commandCount = 0;
		ConfigurationSection alias = cmdCore.getConfig().getConfigurationSection("Aliases");
		for (String cmd : alias.getKeys(false)) {
			aliases.put(cmd, alias.getString("Aliases." + cmd));
			commandCount++;
		}
		cmdCore.getLogger().info(commandCount + " configured alias(es) have been added.");
	}

	public void addConfiguredCommands() {
		int commandCount = 0;

		ConfigurationSection custom = cmdCore.getConfig().getConfigurationSection("Customized");
		for (String cmd : custom.getKeys(false)) {

			List<String> actions = new ArrayList<>();
			if (custom.isSet("Actions")) {
				if (custom.isList("Actions")) {
					actions.addAll(custom.getStringList("Actions"));
				} else {
					actions.add(custom.getString("Actions"));
				}
			}

			double cost = custom.isSet("Cost") ? custom.getDouble("Cost") : 0;
			String invalidFunds = custom.isSet("InvalidFunds_Message") ? custom.getString("InvalidFunds_Message")
					: null;
			String permNode = custom.isSet("Permission") ? custom.getString("Permission") : null;
			String noPerm = custom.isSet("No-Permission") ? custom.getString("No-Permission") : null;

			CustomizedCommand customCMD = new CustomizedCommand(cmd, actions, cost, invalidFunds, permNode, noPerm);
			customCommands.put(cmd, customCMD);
			commandCount++;
		}

		cmdCore.getLogger().info(commandCount + " configured command(s) have been added.");
	}

}
