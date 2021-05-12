package com.thecrappiest.cmdcore.methods;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.thecrappiest.cmdcore.CommandCore;
import com.thecrappiest.cmdcore.item.ItemBuilder;

public class Actions {
	
	public enum Action {
        MESSAGE("message"),
        BROADCAST("broadcast"),
        BROADCAST_TITLE("title"),
        BROADCAST_SUBTITLE("subtitle"),
        BROADCAST_FULLTITLE("fulltitle"),
        PLAYER_TITLE("playerTitle"),
        PLAYER_SUBTITLE("playerSubtitle"),
        PLAYER_FULLTITLE("playerFulltitle"),
        HOTBARSLOT("hotbarSlot"),
        CONSOLE_COMMAND("consoleCommand"),
        PLAYER_COMMAND("playerCommand"),
        PLAYER_CHAT("playerChat"),
        CLOSE_INVENTORY("closeInventory"),
        SEND_SERVER("sendServer"),
        PLAY_SOUND("SOUND"),
        GIVE_ITEM("giveItem");

        private String action;
        Action(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

        public static Action getAction(String s) {
            for (Action action : Action.values()) {
                if(s.equalsIgnoreCase(action.getAction())) {
                    return action;
                }
            }
            return null;
        }
    }

	public static void runAction(Player p, String fullAction, String[] args) {
		MessageUtil msgUtil = MessageUtil.utils();

		if (!fullAction.startsWith("[")) {
			return;
		}

		Action action = null;
		String actionName = null;
		if (fullAction.split(" ").length > 0) {
			actionName = fullAction.split(" ")[0].replaceFirst("\\[", "").replaceFirst("]", "");
			action = Action.getAction(actionName);
		} else {
			actionName = fullAction.split("]")[0].replaceFirst("\\[", "");
			action = Action.getAction(actionName);
		}
		if (action == null) {
			return;
		}

		String performString = msgUtil.addPlaceHolders(p,
				modifyArgs(fullAction.replace("[" + actionName + "] ", "").replace("[" + actionName + "]", ""), args));

		switch (action) {
		case MESSAGE:
			p.sendMessage(msgUtil.color(performString));
			break;

		case PLAYER_TITLE:
			p.sendTitle(msgUtil.color(performString), "", 20, 50, 10);
			break;

		case PLAYER_SUBTITLE:
			p.sendTitle("", msgUtil.color(performString), 20, 50, 10);
			break;

		case PLAYER_FULLTITLE:
			p.sendTitle(msgUtil.color(performString).split("\\|")[0], msgUtil.color(performString).split("\\|")[1], 20,
					50, 10);
			break;

		case PLAYER_COMMAND:
			p.performCommand(performString);
			break;

		case PLAYER_CHAT:
			p.chat(msgUtil.color(performString));
			break;

		case CLOSE_INVENTORY:
			p.closeInventory();
			break;

		case PLAY_SOUND:
			if (Sound.valueOf(performString) != null) {
				p.playSound(p.getLocation(), Sound.valueOf(performString), 1.0F, 1.0F);
			}
			break;

		case GIVE_ITEM:
			CommandCore cmd = CommandCore.getCMDCore();
			ItemStack item = null;
			if (performString.startsWith("%")) {
				String phItem = performString.replace("%", "");
				if (cmd.getConfig().isSet("Place-Holder_Items." + phItem)) {
					item = ItemBuilder.createItem(p,
							cmd.getConfig().getConfigurationSection("Place-Holder_Items." + phItem), null);
				}
			} else {
				item = ItemBuilder.createItem(p, performString, null);
			}
			if (item != null) {
				if (p.getInventory().firstEmpty() == -1) {
					p.getWorld().dropItemNaturally(p.getLocation(), item);
				} else {
					p.getInventory().addItem(item);
				}
			}
			break;

		case HOTBARSLOT:
			p.getInventory().setHeldItemSlot(Integer.valueOf(performString));
			break;

		case BROADCAST:
			Bukkit.broadcastMessage(msgUtil.color(performString));
			break;

		case BROADCAST_TITLE:
			Bukkit.getOnlinePlayers().stream().forEach(player -> {
				player.sendTitle(msgUtil.color(performString), "", 20, 50, 10);
			});
			break;
		case BROADCAST_SUBTITLE:
			Bukkit.getOnlinePlayers().stream().forEach(player -> {
				player.sendTitle("", msgUtil.color(performString), 20, 50, 10);
			});
			break;

		case BROADCAST_FULLTITLE:
			Bukkit.getOnlinePlayers().stream().forEach(player -> {
				player.sendTitle(msgUtil.color(performString).split("\\|")[0],
						msgUtil.color(performString).split("\\|")[1], 20, 50, 10);
			});
			break;

		case CONSOLE_COMMAND:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), performString);
			break;

		case SEND_SERVER:
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(performString);
			p.sendPluginMessage(CommandCore.getCMDCore(), "BungeeCord", out.toByteArray());
			break;
		}
	}

	private static String modifyArgs(String s, String[] args) {
		if (args == null) {
			return s;
		}
		if (args.length == 0) {
			return s;
		}

		if (s.contains(" ")) {

			for (int i = 0; i < args.length; i++) {
				s = s.replace("%arg_" + (i + 1) + "%", args[i]);
			}

		} else {
			if (s.equalsIgnoreCase("%arg_1%") && args.length > 0) {
				s = s.replace("%arg_1%", args[0]);
			}
		}

		if (s.contains("%arg_all%")) {
			StringBuilder build = new StringBuilder();
			for (String arg : args) {
				build.append(arg).append(" ");
			}

			s = s.replace("%arg_all%", build.substring(0, build.length() - 1));
		}

		return s;
	}

}
