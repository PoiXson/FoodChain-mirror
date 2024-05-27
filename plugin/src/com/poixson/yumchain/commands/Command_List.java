package com.poixson.yumchain.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.poixson.tools.commands.pxnCommand;
import com.poixson.yumchain.YumChainDAO;
import com.poixson.yumchain.YumChainPlugin;


// /yumchain list
public class Command_List extends pxnCommand {

	protected final YumChainPlugin plugin;



	public Command_List(final YumChainPlugin plugin) {
		super(
			"list"
		);
		this.plugin = plugin;
	}



	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) {
//TODO: permissions for .other
		final Player player = (sender instanceof Player ? (Player)sender : null);
		if (player == null) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		if (!player.hasPermission("yumchain.cmd.list")) return false;
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
				ChatColor.GREEN, (ate ? "x" : "_"),
				ChatColor.WHITE, name
			));
		}
		player.sendMessage(msg.toString());
		return true;
	}



	@Override
	public List<String> onTabComplete(final CommandSender sender, final String[] args) {
//TODO
System.out.println("TAB:"); for (final String arg : args) System.out.println("  "+arg);
return null;
	}



}
