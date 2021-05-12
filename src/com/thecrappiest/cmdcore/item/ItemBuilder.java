package com.thecrappiest.cmdcore.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.thecrappiest.cmdcore.CommandCore;
import com.thecrappiest.cmdcore.methods.MessageUtil;
import com.thecrappiest.cmdcore.methods.NBTUtil;

public class ItemBuilder {

	@SuppressWarnings("deprecation")
	public static ItemStack createItem(Player p, ConfigurationSection cs, Map<String, String> placeholders) {
		Material mat = Material.BARRIER;
		String name = "";
		List<String> lore = new ArrayList<>();
		boolean glow = false;
		boolean hideAttributes = false;
		short durability = 0;

		MessageUtil msgUtil = MessageUtil.utils();

		for (String pathKey : cs.getKeys(false)) {
			String value = cs.getString(pathKey);
			switch (pathKey) {
			case "Material":
				if (Material.valueOf(value.toUpperCase()) != null) {
					mat = Material.valueOf(value.toUpperCase());
				}
				break;

			case "Name":
				name = value.equals("") ? null : msgUtil.color(msgUtil.addPlaceHolders(p, value));
				break;

			case "Lore":
				if (!value.equals("")) {
					List<String> loreEdit = new ArrayList<>();
					for (String loreLine : cs.getStringList(pathKey)) {
						loreEdit.add(msgUtil.color(loreLine));
					}
					loreEdit = msgUtil.addPlaceHolders(p, loreEdit);
					lore = loreEdit;
				}
				break;

			case "Glow":
				if (CommandCore.glow != null) {
					glow = cs.getBoolean(pathKey);
				}
				break;

			case "HideAttributes":
				hideAttributes = cs.getBoolean(pathKey);
				break;

			case "Durability":
				durability = Integer.valueOf(cs.getString(pathKey)).shortValue();
				break;
			}
		}

		ItemStack item = new ItemStack(mat);

		if (cs.contains("Skull")) {
			item = SkullCreation.getInstance().createSkullItem(msgUtil.addPlaceHolders(p, cs.getString("Skull")));
		}

		ItemMeta itemMeta = item.getItemMeta();

		if (name == null) {
			itemMeta.setDisplayName(msgUtil.color("&f"));
		} else {
			itemMeta.setDisplayName(name);
		}

		if (!lore.isEmpty()) {
			itemMeta.setLore(lore);
		}

		if (glow) {
			itemMeta.addEnchant(CommandCore.glow, 1, true);
		}

		if (hideAttributes) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}

		item.setItemMeta(itemMeta);

		if (cs.contains("Enchants")) {
			item = addEnchants(cs.getStringList("Enchants"), item);
		}

		String materialName = mat.name();
		if (materialName.contains("LEATHER") && (materialName.contains("HELMET") || materialName.contains("CHESTPLATE")
				|| materialName.contains("LEGGINGS") || materialName.contains("BOOTS"))) {
			if (cs.contains("Color")) {
				item = ArmorColor.setColor(item, cs.getString("Color"));
			}
		}

		if (cs.contains("NBTTags")) {
			for (String NBTKey : cs.getConfigurationSection("NBTTags").getKeys(true)) {
				item = NBTUtil.getNBTUtils().addNBTTagString(item, NBTKey, cs.getString("NBTTags." + NBTKey), null);
			}
		}

		if (durability > 0) {
			item.setDurability(durability);
		}

		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack createItem(Player p, String data, Map<String, String> placeholders) {
		Material mat = Material.BARRIER;
		String name = null;
		List<String> lore = null;
		boolean glow = false;
		boolean hideAttributes = false;
		List<String> enchants = null;
		String color = null;
		short durability = 0;

		MessageUtil msgUtil = MessageUtil.utils();

		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta itemMeta = item.getItemMeta();

		JSONObject jsonData = null;
		try {
			jsonData = (JSONObject) new JSONParser().parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (jsonData != null) {
			for (Object entry : jsonData.keySet()) {
				String key = String.valueOf(entry);
				String value = String.valueOf(jsonData.get(key));
				switch (key) {
				case "Material":
					if (Material.valueOf(value.toUpperCase()) != null) {
						mat = Material.valueOf(value.toUpperCase());
						item.setType(mat);
					}
					break;
				case "Skull":
					item = SkullCreation.instance.createSkullItem(msgUtil.addPlaceHolders(p, value));
					itemMeta = item.getItemMeta();
					break;
				case "Name":
					name = msgUtil.color(msgUtil.addPlaceHolders(p, value));
					break;
				case "Lore":
					List<String> loreFromData = new ArrayList<>();
					for (String line : value.split("\\|")) {
						loreFromData.add(String.valueOf(msgUtil.color(msgUtil.addPlaceHolders(p, line))));
					}
					lore = loreFromData;
					break;
				case "Enchants":
					List<String> enchantsFromData = new ArrayList<>();
					for (String line : value.split(",")) {
						enchantsFromData.add(line);
					}
					enchants = enchantsFromData;
					break;
				case "Glow":
					if (glow) {
						itemMeta.addEnchant(CommandCore.glow, 1, true);
					}
					break;
				case "HideAttributes":
					hideAttributes = Boolean.valueOf(value);
					break;
				case "Color":
					color = value;
					break;
				case "Durability":
					durability = Integer.valueOf(value).shortValue();
					break;
				}
			}
		}

		if (name == null) {
			itemMeta.setDisplayName(msgUtil.color("&f"));
		} else {
			itemMeta.setDisplayName(name);
		}

		if (lore != null && !lore.isEmpty()) {
			itemMeta.setLore(lore);
		}

		if (glow) {
			itemMeta.addEnchant(CommandCore.glow, 1, true);
		}

		if (hideAttributes) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}

		item.setItemMeta(itemMeta);

		if (enchants != null) {
			item = addEnchants(enchants, item);
		}

		String materialName = mat.name();
		if (materialName.contains("LEATHER") && (materialName.contains("HELMET") || materialName.contains("CHESTPLATE")
				|| materialName.contains("LEGGINGS") || materialName.contains("BOOTS"))) {
			if (color != null) {
				item = ArmorColor.setColor(item, color);
			}
		}

		if (durability > 0) {
			item.setDurability(durability);
		}

		return item;
	}

	public static ItemStack addEnchants(List<String> enchants, ItemStack item) {
		for (String eandl : enchants) {
			String enchant = eandl.split("\\|")[0];
			int level = Integer.valueOf(eandl.split("\\|")[1]);
			if (Enchantment.getByKey(NamespacedKey.minecraft(enchant)) != null) {
				item.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchant)), level);
			}
		}
		return item;
	}

}
