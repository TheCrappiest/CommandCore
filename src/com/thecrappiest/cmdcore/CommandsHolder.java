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
			aliases.put(cmd, alias.getString(cmd));
			commandCount++;
		}
		cmdCore.getLogger().info(commandCount + " configured alias(es) have been added.");
	}

	public void addConfiguredCommands() {
		int commandCount = 0;

		ConfigurationSection custom = cmdCore.getConfig().getConfigurationSection("Customized");
		for (String cmd : custom.getKeys(false)) {

			List<String> actions = new ArrayList<>();
			if (custom.isSet(cmd+".Actions")) {
				if (custom.isList(cmd+".Actions")) {
					actions.addAll(custom.getStringList(cmd+".Actions"));
				} else {
					actions.add(custom.getString(cmd+".Actions"));
				}
			}

			double cost = custom.isSet(cmd+".Cost") ? custom.getDouble(cmd+".Cost") : 0;
			String invalidFunds = custom.isSet(cmd+".InvalidFunds_Message") ? custom.getString(cmd+".InvalidFunds_Message")
					: null;
			String permNode = custom.isSet(cmd+".Permission") ? custom.getString(cmd+".Permission") : null;
			String noPerm = custom.isSet(cmd+".No-Permission") ? custom.getString(cmd+".No-Permission") : null;

			CustomizedCommand customCMD = new CustomizedCommand(cmd, actions, cost, invalidFunds, permNode, noPerm);
			customCommands.put(cmd, customCMD);
			commandCount++;
		}

		cmdCore.getLogger().info(commandCount + " configured command(s) have been added.");
	}

}
