package com.github.qianniancc.itemgocmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.winterguardian.easyscoreboards.ScoreboardUtil;

public class ItemGoCmd extends JavaPlugin implements Listener {
	public boolean isSBEnable = getServer().getPluginManager().getPlugin("EasyScoreboards") != null;
	public ConfigurationSection lang = getConfig().getConfigurationSection("lang");
	public Set<String> set0 = getConfig().getConfigurationSection("settings").getKeys(false);
	public ConfigurationSection sett = getConfig().getConfigurationSection("settings");

	public void onEnable() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveDefaultConfig();
		}
		if (lang.getString("textNotItemHand") == null) {
			lang.set("textNotItemHand", lang.getString("textNotItemHand"));
			saveConfig();
		}
		getLogger().info("启动成功！");
		getServer().getPluginManager().registerEvents(this, this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("本指令必须由一个玩家执行");
			return true;
		}
		String TextGiveSB = this.lang.getString("GiveSB");
		String GiveSB = chatToColor(TextGiveSB);
		String ReloadOK = this.lang.getString("ReloadOK");
		final Player p = (Player) sender;
		// Reload
		if (cmd.getName().equalsIgnoreCase("igcreload")) {
			if ((!sender.isOp()) && (!sender.hasPermission("igc.reload")) && (!sender.hasPermission("igc.*"))) {
				String TextNotReloadPerm = this.lang.getString("NotReloadPerm");
				String NotReloadPerm = chatToColor(TextNotReloadPerm);
				sender.sendMessage(NotReloadPerm);
				return false;
			}
			reloadConfig();
			String TextReloadOK = this.lang.getString("ReloadOK");
			ReloadOK = chatToColor(TextReloadOK);
			sender.sendMessage(ReloadOK);
			if ((this.isSBEnable) && (p.isOnline())) {
				ScoreboardUtil.unrankedSidebarDisplay(p, new String[] { GiveSB, ReloadOK });
			}
			new BukkitRunnable() {
				int x = 0;

				public void run() {
					if (this.x >= 20) {
						cancel();
					} else {
						if ((ItemGoCmd.this.isSBEnable) && (p.isOnline())) {
							ScoreboardUtil.unrankedSidebarDisplay(p, new String[0]);
						}
						this.x += 1;
					}
				}
			}.runTaskLaterAsynchronously(this, 100L);

			return true;
		}
		// List
		if (cmd.getName().equalsIgnoreCase("igclist")) {
			if ((!sender.isOp()) && (!sender.hasPermission("igc.list")) && (!sender.hasPermission("igc.*"))) {
				sender.sendMessage("§c§l你没有权限执行这个指令");
				return false;
			}
			sender.sendMessage("§e§l物品列表:");
			for (String str : this.set0) {
				if (!str.contains(".")) {
					sender.sendMessage(str);
				}
			}
		}
		// Give
		if (cmd.getName().equalsIgnoreCase("igcgive")) {
			if (args.length < 1) {
				sender.sendMessage("§c§l参数不足，请输入/igcgive <物品名>");
				return true;
			}
			if ((!sender.isOp()) && (!sender.hasPermission("igc.give")) && (!sender.hasPermission("igc.*"))) {
				String TextNotGivePerm = this.lang.getString("NotGivePerm");
				String NotGivePerm = chatToColor(TextNotGivePerm);
				sender.sendMessage(NotGivePerm);
				return false;
			}
			try {
				this.sett.getConfigurationSection(args[0]);
			} catch (Exception e) {
				sender.sendMessage("不存在这个物品，请输入/igclist查看");
			}
			String itemIdString = this.sett.getConfigurationSection(args[0]).getString("ItemId");
			int giveitemId = Integer.parseInt(itemIdString);

			ItemStack giveitem = new ItemStack(giveitemId);

			ItemMeta giveItemMeta = giveitem.getItemMeta();
			String TextDisplayName = this.sett.getConfigurationSection(args[0]).getString("DisplayName");
			String DisplayName = chatToColor(TextDisplayName);
			giveItemMeta.setDisplayName(DisplayName);
			if (this.sett.getConfigurationSection(args[0]).getBoolean("onLore")) {
				ArrayList<String> lores = new ArrayList<String>();
				String TextLore = this.sett.getConfigurationSection(args[0]).getString("Lore");
				String Lore = chatToColor(TextLore);
				lores.add(Lore);
				giveItemMeta.setLore(lores);
			}
			giveitem.setItemMeta(giveItemMeta);
			p.getInventory().addItem(new ItemStack[] { giveitem });
			String TextGiveOK = this.lang.getString("GiveOK");
			String GiveOK = chatToColor(TextGiveOK);
			sender.sendMessage(GiveOK);
			if ((this.isSBEnable) && (p.isOnline())) {
				ScoreboardUtil.unrankedSidebarDisplay(p, new String[] { GiveSB, GiveOK });
			}
			new BukkitRunnable() {
				int x = 0;

				public void run() {
					if (this.x >= 20) {
						cancel();
					} else {
						if ((ItemGoCmd.this.isSBEnable) && (p.isOnline())) {
							ScoreboardUtil.unrankedSidebarDisplay(p, new String[0]);
						}
						this.x += 1;
					}

				}
			}.runTaskLaterAsynchronously(this, 100L);
			return true;
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCmd(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage().trim();
		if (cmd.startsWith("/")) {
			cmd = cmd.substring(1).trim();
		}
		int do0 = 0;
		for (String str : this.set0) {
			if (!str.contains(".")) {
				ConfigurationSection items = getConfig().getConfigurationSection("settings")
						.getConfigurationSection(str);
				java.util.List<String> cmds = items.getStringList("Cmd");
				for (String cmd0 : cmds) {
					if (cmd.startsWith(cmd0)) {
						do0 = 1;
					}
				}
				if (do0 == 1) {
					ItemStack item = e.getPlayer().getItemInHand();
					int itemAmount = item.getAmount();
					String TextNotItemHand = lang.getString("textNotItemHand");
					TextNotItemHand = TextNotItemHand.replaceFirst("%cmd%", "/" + cmd);
					TextNotItemHand = TextNotItemHand.replaceFirst("%item%", items.getString("DisplayName"));
					String NotItemHand = chatToColor(TextNotItemHand);
					if (item.getType() != Material.AIR) {
						boolean islore = false;
						if (items.getBoolean("onLore")) {
							ArrayList<String> lores = new ArrayList<String>();
							String TextLore = items.getString("Lore");
							String Lore = chatToColor(TextLore);
							lores.add(Lore);
							try {
								islore = !lores.toString().equalsIgnoreCase(item.getItemMeta().getLore().toString());
							} catch (Exception e1) {
								islore = false;
							}
						}
						String TextDisplayName = items.getString("DisplayName");
						String DisplayName = chatToColor(TextDisplayName);
						if ((!DisplayName.equals(item.getItemMeta().getDisplayName())) || (islore)) {
							if ((!e.getPlayer().isOp()) && (!e.getPlayer().hasPermission("igc.noigc"))
									&& (!e.getPlayer().hasPermission("igc.*"))) {
								e.setCancelled(true);
								e.getPlayer().sendMessage(NotItemHand);
							}
						} else {
							ItemStack setitem = e.getPlayer().getItemInHand();
							setitem.setAmount(itemAmount - 1);
							e.getPlayer().setItemInHand(setitem);
						}

					} else if ((!e.getPlayer().isOp()) && (!e.getPlayer().hasPermission("igc.noigc"))
							&& (!e.getPlayer().hasPermission("igc.*"))) {
						e.setCancelled(true);
						e.getPlayer().sendMessage(NotItemHand);
					}
				}

				do0 = 0;
			}
		}
	}

	private String chatToColor(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);

	}
}
