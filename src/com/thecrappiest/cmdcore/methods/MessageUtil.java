package com.thecrappiest.cmdcore.methods;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.thecrappiest.cmdcore.CommandCore;

import me.clip.placeholderapi.PlaceholderAPI;

public class MessageUtil {

	public static MessageUtil utils() {
		return new MessageUtil();
	}

	private boolean usePlaceHolderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceHolderAPI");
	private FileConfiguration cc = CommandCore.getCMDCore().getConfig();

	public String color(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public String addPlaceHolders(Player p, String s) {
		if (usePlaceHolderAPI) {
			return addCustomPlaceholders(PlaceholderAPI.setPlaceholders(p, s));
		}
		return addCustomPlaceholders(s);
	}

	public List<String> addPlaceHolders(Player p, List<String> s) {
		List<String> edit = new ArrayList<>();
		if (usePlaceHolderAPI) {
			for (String line : s) {
				edit.add(addCustomPlaceholders(PlaceholderAPI.setPlaceholders(p, line)));
			}
			return edit;
		}
		return addCustomPlaceholders(s);
	}

	public String addCustomPlaceholders(String s) {
		for (String placeholder : cc.getConfigurationSection("Custom_Place-Holders").getKeys(false)) {
			s = s.replace("%" + placeholder + "%", cc.getString("Custom_Place-Holders." + placeholder));
		}
		return s;
	}

	public List<String> addCustomPlaceholders(List<String> s) {
		List<String> edit = new ArrayList<>();
		for (String line : s) {
			String current = line;
			for (String placeholder : cc.getConfigurationSection("Custom_Place-Holders").getKeys(false)) {
				current = current.replace("%" + placeholder + "%", cc.getString("Custom_Place-Holders." + placeholder));
			}
			edit.add(current);
		}
		return edit;
	}

}
