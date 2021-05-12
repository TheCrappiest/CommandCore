package com.thecrappiest.cmdcore.methods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTUtil {

	private static NBTUtil nbtUtils = getNBTUtils();

	@SuppressWarnings("rawtypes")
	private Class CISClass, nbtTagCompound;
	private Method CISNMSCopyMethod, CISCraftMirrorMethod, CISNBTGetTag, CISNBTSetTag, GetObject, SetString, GetString,
			GetList, SetInt, GetInt, GetByteArray, HasKey, nbtC;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private NBTUtil() {
		String version = Bukkit.getServer().getClass().getPackage().getName();
		version = version.substring(version.lastIndexOf(".") + 1, version.length());
		try {
			CISClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
			CISNMSCopyMethod = CISClass.getMethod("asNMSCopy", ItemStack.class);
			nbtTagCompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
			Object nbtTag = nbtTagCompound.newInstance();
			ItemStack normalItem = new ItemStack(Material.COBBLESTONE);
			Object nmsItemStack = CISNMSCopyMethod.invoke(CISClass, normalItem);

			Class nmsISClass = nmsItemStack.getClass();
			Class nbtClass = nbtTag.getClass();
			Class s = String.class;

			CISCraftMirrorMethod = CISClass.getMethod("asCraftMirror", nmsISClass);
			CISNBTGetTag = nmsISClass.getMethod("getTag");
			CISNBTSetTag = nmsISClass.getMethod("setTag", nbtClass);
			GetObject = nbtClass.getMethod("get", s);
			SetString = nbtClass.getMethod("setString", s, s);
			GetString = nbtClass.getMethod("getString", s);
			SetInt = nbtClass.getMethod("setInt", s, int.class);
			GetInt = nbtClass.getMethod("getInt", s);
			GetByteArray = nbtClass.getMethod("getByteArray", s);
			HasKey = nbtClass.getMethod("hasKey", s);
			nbtC = nbtClass.getMethod("getKeys");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static NBTUtil getNBTUtils() {
		if (nbtUtils == null) {
			nbtUtils = new NBTUtil();
		}
		return nbtUtils;
	}

	public Object getNMSItemStack(ItemStack itemStack) {
		try {
			return CISNMSCopyMethod.invoke(CISClass, itemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ItemStack getNormalItemStack(Object nmsItemStack) {
		try {
			return (ItemStack) CISCraftMirrorMethod.invoke(CISClass, nmsItemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getNBTTag(Object nmsItemStack) {
		try {
			return CISNBTGetTag.invoke(nmsItemStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setNBTTag(Object nmsItemStack, Object nbtTag) {
		try {
			CISNBTSetTag.invoke(nmsItemStack, nbtTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasKey(ItemStack itemStack, String key) throws Exception {
		Object nbtIS = getNMSItemStack(itemStack);
		if (nbtIS == null) {
			return false;
		}

		Object nbtTag = getNBTTag(nbtIS);
		if (nbtTag == null) {
			return false;
		}
		return (Boolean) HasKey.invoke(nbtTag, key);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getTagKeys(ItemStack itemStack) {
		try {
			Object nbtIS = getNMSItemStack(itemStack);
			if (nbtIS == null) {
				return new ArrayList<String>();
			}

			Object nbtTag = getNBTTag(nbtIS);
			if (nbtTag == null) {
				return new ArrayList<String>();
			}

			Set<String> tags = (Set<String>) nbtC.invoke(nbtTag);
			ArrayList<String> tagArray = new ArrayList<String>(tags);
			return tagArray;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	public String getStringFromTag(Object nbtTag, String key) {
		try {
			return (String) GetString.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setTagString(Object NBTTag, String key, String value) {
		try {
			SetString.invoke(NBTTag, key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer getIntFromTag(Object nbtTag, String key) {
		try {
			return (Integer) GetInt.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setTagInt(Object nbtTag, String key, Integer i) {
		try {
			SetInt.invoke(nbtTag, key, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getByteArrayFromTag(Object nbtTag, String key) {
		try {
			return (byte[]) GetByteArray.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getObjectFromTag(Object nbtTag, String key) {
		try {
			return (Object) GetObject.invoke(nbtTag, key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public List getListFromTag(Object nbtTag, String key, Integer listType) {
		try {
			return (List) GetList.invoke(nbtTag, key, listType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ItemStack addNBTTagString(ItemStack itemstack, String key, String value, Map<String, String> tagEntries) {
		ItemStack nbtItem = itemstack.clone();
		Object nbtItemStack = getNMSItemStack(nbtItem);
		Object NBTTag;
		try {
			NBTTag = getNBTTag(nbtItemStack) != null ? CISNBTGetTag.invoke(nbtItemStack) : nbtTagCompound.newInstance();
			setTagString(NBTTag, key, value);
			if (tagEntries != null && !tagEntries.isEmpty()) {
				for (Entry<String, String> entries : tagEntries.entrySet()) {
					String entryKey = entries.getKey();
					String entryValue = entries.getValue();

					setTagString(NBTTag, entryKey, entryValue);
				}
			}
			setNBTTag(nbtItemStack, NBTTag);

			nbtItem = getNormalItemStack(nbtItemStack);
			return nbtItem;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Object> getNBTTags(ItemStack item) {
		Map<String, Object> tags = new HashMap<>();
		for (String key : getTagKeys(item)) {
			Object o = getObjectFromTag(getNBTTag(getNMSItemStack(item)), key);
			String s = getStringFromTag(getNBTTag(getNMSItemStack(item)), key);
			int i = getIntFromTag(getNBTTag(getNMSItemStack(item)), key);
			if (o != null) {
				tags.put(key, o);
			} else {
				if (s != null) {
					tags.put(key, s);
				}
				tags.put(key, i);
			}
		}
		return tags;
	}

	public boolean itemContainsNBTTag(ItemStack itemstack, String key) {
		try {
			return (hasKey(itemstack, key));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getStringFromNBT(ItemStack itemstack, String key) {
		if (!itemContainsNBTTag(itemstack, key)) {
			return null;
		}

		return getStringFromTag(getNBTTag(getNMSItemStack(itemstack)), key);
	}

}
