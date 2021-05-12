package com.thecrappiest.cmdcore.item;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.thecrappiest.cmdcore.CommandCore;

public class SkullCreation {

	private CommandCore cmdCore = CommandCore.getCMDCore();
	public static SkullCreation instance;

	public static SkullCreation getInstance() {
		if (instance == null) {
			instance = new SkullCreation();
		}
		return instance;
	}

	public Map<String, ItemStack> loadedSkulls = new HashMap<>();
	public Map<String, String> savedTexturesData = new HashMap<>();
	public Map<String, String> nameToUUID = new HashMap<>();

	public String getUUIDFromUsername(String name) {

		if (nameToUUID.containsKey(name)) {
			return nameToUUID.get(name);
		}

		if (Bukkit.getPlayerExact(name) != null && Bukkit.getOnlineMode()) {
			String uuid = Bukkit.getPlayerExact(name).getUniqueId().toString().replace("-", "");
			nameToUUID.put(name, uuid);
			return uuid;
		}

		try {
			String UUIDJson = "";
			URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStream inputStream = url.openStream();

			try {
				UUIDJson = org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils.toString(inputStream, "UTF-8");
			} catch (Exception e) {
				cmdCore.getLogger().warning("[Mojang API] Error on data retrieving. (" + name + ")");
				e.printStackTrace();
			}
			inputStream.close();

			if (UUIDJson.isEmpty()) {
				inputStream.close();
				cmdCore.getLogger().warning("[Mojang API] Returned no data from the username. (" + name + ")");
				return null;
			}

			JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
			inputStream.close();
			nameToUUID.put(name, UUIDObject.get("id").toString());
			return UUIDObject.get("id").toString();
		} catch (IOException | ParseException e) {
			return null;
		}
	}

	public String getPlayerSkullTexture(String uuid) {

		if (savedTexturesData.containsKey(uuid)) {
			return savedTexturesData.get(uuid);
		}

		try {
			String UUIDJson = "";
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
			InputStream inputStream = url.openStream();

			try {
				UUIDJson = org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils.toString(inputStream, "UTF-8");
			} catch (Exception e) {
				cmdCore.getLogger().warning("[Mojang API] Error on data uuid retrieving. (" + uuid + ")");
				e.printStackTrace();
			}
			inputStream.close();

			if (UUIDJson.isEmpty()) {
				inputStream.close();
				cmdCore.getLogger().warning("[Mojang API] Returned no data from the uuid. (" + uuid + ")");
				return null;
			}

			JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
			inputStream.close();
			JSONArray properties = (JSONArray) UUIDObject.get("properties");
			JSONObject propertiesArray = (JSONObject) properties.get(0);
			String texturesvalue = propertiesArray.get("value").toString();

			if (texturesvalue != null) {
				savedTexturesData.put(uuid, texturesvalue);
				return texturesvalue;
			} else {
				cmdCore.getLogger().warning("[Mojang API] No texture value found. (" + uuid + ")");
				cmdCore.getLogger().warning("[Mojang API] Possible cause -> To many requests.");
				return null;
			}
		} catch (ParseException | IOException e) {
			cmdCore.getLogger().warning("[Mojang API] Could not open API Stream (" + uuid + ")");
			cmdCore.getLogger().warning("[Mojang API] Possible causes;");
			cmdCore.getLogger().warning(
					"[Mojang API] To many API requests, SessionServer may be down, UUID got passed username check.");
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public ItemStack createSkullItem(String username) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();

		if (username.startsWith("MHF")) {
			headMeta.setOwner(username);
			head.setItemMeta(headMeta);
			return head;
		}

		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = null;
		if (username.toLowerCase().startsWith("base64:")) {
			encodedData = username.split(":")[1].getBytes();
		} else {
			encodedData = getPlayerSkullTexture(getUUIDFromUsername(username)).getBytes();
		}
		if (encodedData != null) {
			profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
			Field profileField = null;
			try {
				profileField = headMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(headMeta, profile);
			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			head.setItemMeta(headMeta);
			return head;
		}
		return head;
	}

}
