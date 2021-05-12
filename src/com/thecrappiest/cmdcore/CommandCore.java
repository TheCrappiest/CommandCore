package com.thecrappiest.cmdcore;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.thecrappiest.cmdcore.item.GlowingEnchant;
import com.thecrappiest.cmdcore.listeners.PerformCommand;

import net.milkbowl.vault.economy.Economy;

public class CommandCore extends JavaPlugin {

	public static CommandCore instance;

	public void onEnable() {
		instance = this;

		if (!getConfig().isSet("Aliases")) {
			getConfig().options().copyDefaults(true);
			saveConfig();
			getLogger().info("Aliases path not found. Default config paths loaded.");
		}

		getLogger().info("CommandCore config has been reloaded.");
		reloadConfig();

		getCommand("commandcore").setExecutor(new BaseCommand());
		CommandsHolder.retrieveCommandsHolder().addConfiguredAliases();
		CommandsHolder.retrieveCommandsHolder().addConfiguredCommands();

		new PerformCommand(this);

		registerGlowEnchant();

		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			setupEconomy();
		}
	}

	public void onDisable() {
		CommandsHolder.retrieveCommandsHolder().aliases.clear();
		CommandsHolder.retrieveCommandsHolder().customCommands.clear();
		saveConfig();
	}

	public static CommandCore getCMDCore() {
		return instance;
	}

	public Economy economy = null;

	public boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	public boolean hasAmount(OfflinePlayer op, double a) {
		return (economy.getBalance(op) >= a);
	}

	public void giveAmount(OfflinePlayer op, double a) {
		economy.depositPlayer(op, a);
	}

	public static GlowingEnchant glow = null;

	public static void registerGlowEnchant() {
		if (glow == null) {
			glow = new GlowingEnchant(new NamespacedKey(instance, "GlowingEnchant"));
			if (Enchantment.getByKey(new NamespacedKey(instance, "GlowingEnchant")) != null) {
				return;
			}
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
				Enchantment.registerEnchantment(glow);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
