package com.thecrappiest.cmdcore.item;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class GlowingEnchant extends Enchantment {

	public GlowingEnchant(NamespacedKey id) {
		super(id);
	}

	@Override public boolean canEnchantItem(ItemStack arg0) { return false; }
	@Override public boolean conflictsWith(Enchantment arg0) { return false; }
	@Override public EnchantmentTarget getItemTarget() { return null; }
	@Override public int getMaxLevel() { return 0; }
	@Override public String getName() { return null; }
	@Override public int getStartLevel() { return 0; }
	@Override public boolean isCursed() { return false; }
	@Override public boolean isTreasure() { return false; }
	
}
