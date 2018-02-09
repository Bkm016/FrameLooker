package me.skymc.framelook;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.skymc.taboolib.display.TitleUtils;
import me.skymc.taboolib.fileutils.ConfigUtils;
import me.skymc.taboolib.message.MsgUtils;
import me.skymc.taboolib.sound.SoundPack;

/**
 * @author sky
 * @since 2018��2��8�� ����1:22:20
 */
public class Main extends JavaPlugin implements Listener {
	
	private static SoundPack error;
	private static SoundPack success;
	
	@Getter
	private FileConfiguration config;
	
	@Override
	public void reloadConfig() {
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveResource("config.yml", true);
		}
		config = ConfigUtils.load(this, file);
		
		// ������Ч
		error = new SoundPack(config.getString("Sound.error"));
		success = new SoundPack(config.getString("Sound.success"));
	}
	
	@Override
	public void onEnable() {
		reloadConfig();
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		// С���
		MsgUtils.send("���������", this);
		MsgUtils.send("����: &f����", this);
		MsgUtils.send("QQ: &f449599702", this);
	}
	
	@Override
	public void onDisable() {
		closeInventory();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		closeInventory();
		reloadConfig();
		sender.sendMessage("reload ok!");
		return true;
	}
	
	@EventHandler
	public void click(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) e.getRightClicked();
			if (frame.getItem() == null || frame.getItem().getType().equals(Material.MAP) || !e.getPlayer().isSneaking()) {
				return;
			}
			
			// ȡ���¼�
			e.setCancelled(true);
			
			if (frame.getItem().getItemMeta().hasLore() && frame.getItem().getItemMeta().getLore().toString().contains(config.getString("Settings.deny-lore"))) {
				// ��Ч
				error.play(e.getPlayer());
				// ����
				TitleUtils.sendTitle(e.getPlayer(), config.getString("Settings.deny-title.title"), 10, 20, 10, config.getString("Settings.deny-title.subtitle"), 10, 20, 10);
			}
			else {
				// ����
				Inventory inv = Bukkit.createInventory(null, 9, config.getString("Settings.title").replace("&", "��"));
				inv.setItem(4, frame.getItem());
				e.getPlayer().openInventory(inv);
				
				// ��Ч
				success.play(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void click(InventoryClickEvent e) {
		if (e.getInventory().getTitle().equals(config.getString("Settings.title").replace("&", "��"))) {
			e.setCancelled(true);
		}
	}
	
	public void closeInventory() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getTitle().equals(getConfig().getString("Settings.title").replace("&", "��"))) {
				player.closeInventory();
			}
		}
	}
}
