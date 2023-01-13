package com.poixson.yumchain.commands;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.poixson.commonmc.tools.commands.pxnCommand;
import com.poixson.yumchain.YumChainDAO;
import com.poixson.yumchain.YumChainPlugin;


public class CommandList extends pxnCommand {

	protected final YumChainPlugin plugin;

	protected final String logPrefix;
	protected final String chatPrefix;



	public CommandList(final YumChainPlugin plugin) {
		super(
			"list"
		);
		this.plugin = plugin;
		this.logPrefix  = plugin.getLogPrefix();
		this.chatPrefix = plugin.getChatPrefix();
	}



	@Override
	public boolean isDefault() {
		return true;
	}



	@Override
	public boolean run(final CommandSender sender,
			final Command cmd, final String[] args) {
		final Player player = (sender instanceof Player ? (Player)sender : null);
		if (player == null) {
			sender.sendMessage(this.logPrefix + "Only players can use this command.");
			return true;
		}
		if (!player.hasPermission("yumchain.list")) {
			player.sendMessage(this.chatPrefix + "You don't have permission to use this.");
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
