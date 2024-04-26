package com.poixson.yumchain.commands;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.poixson.tools.commands.xCMD_Labels;
import com.poixson.yumchain.YumChainDAO;
import com.poixson.yumchain.YumChainPlugin;


public class Command_List extends xCMD_Labels {

	protected final YumChainPlugin plugin;



	public Command_List(final YumChainPlugin plugin) {
		super("list");
		this.plugin = plugin;
	}



	@Override
	public boolean run(final CommandSender sender, final String[] args) {
		final Player player = (sender instanceof Player ? (Player)sender : null);
		if (player == null) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		if (!player.hasPermission("yumchain.list")) {
			player.sendMessage("You don't have permission to use this.");
			return true;
		}
		final YumChainDAO chain = this.plugin.getYumChain(player);
		final StringBuilder msg = new StringBuilder();
		msg.append("\n")
			.append(ChatColor.GREEN)
			.append("Foods:\n");
		final Iterator<Entry<Material, Boolean>> it = chain.foods.entrySet().iterator();
		while (it.hasNext()) {
			final Entry<Material, Boolean> entry = it.next();
			final String name = entry.getKey().name();
			final boolean ate = entry.getValue().booleanValue();
			msg.append(String.format(
				"  %s[%s]%s %s\n",
				ChatColor.GREEN,
				(ate ? "x" : "_"),
				ChatColor.WHITE,
				name
			));
		}
		player.sendMessage(msg.toString());
		return true;
	}



}
