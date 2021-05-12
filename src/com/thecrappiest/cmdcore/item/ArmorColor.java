package com.thecrappiest.cmdcore.item;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorColor {

	public static ItemStack getArmor(String paramCO, String paramIT) {
		ItemStack armorpiece = null;

		switch (paramIT) {
		case "CHESTPLATE":
			armorpiece = new ItemStack(Material.LEATHER_CHESTPLATE);
			break;
		case "LEGGINGS":
			armorpiece = new ItemStack(Material.LEATHER_LEGGINGS);
			break;
		case "BOOTS":
			armorpiece = new ItemStack(Material.LEATHER_BOOTS);
			break;
		}

		armorpiece = setColor(armorpiece, paramCO);
		return armorpiece;
	}

	public static ItemStack setColor(ItemStack itemstack, String color) {
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) itemstack.getItemMeta();
		if (color != null) {
			if (color.contains(",") && color.split(",").length == 3) {
				String[] rgb = color.split(",");
				leatherarmormeta.setColor(
						Color.fromRGB(Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])));
			} else {
				switch (color.toUpperCase()) {
				case "YELLOW":
					leatherarmormeta.setColor(Color.fromRGB(255, 255, 85));
					break;
				case "ORANGE":
					leatherarmormeta.setColor(Color.fromRGB(255, 157, 0));
					break;
				case "BLUE":
					leatherarmormeta.setColor(Color.fromRGB(0, 20, 255));
					break;
				case "LIGHTBLUE":
					leatherarmormeta.setColor(Color.fromRGB(0, 249, 255));
					break;
				case "RED":
					leatherarmormeta.setColor(Color.fromRGB(255, 0, 26));
					break;
				case "GREEN":
					leatherarmormeta.setColor(Color.fromRGB(29, 102, 0));
					break;
				case "WHITE":
					leatherarmormeta.setColor(Color.fromRGB(255, 255, 255));
					break;
				case "PURPLE":
					leatherarmormeta.setColor(Color.fromRGB(128, 0, 128));
					break;
				case "BLACK":
					leatherarmormeta.setColor(Color.fromRGB(0, 0, 0));
					break;
				}
			}
		}

		itemstack.setItemMeta(leatherarmormeta);
		return itemstack;
	}

}
