package com.poixson.yumchain.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.poixson.tools.commands.pxnCommand;
import com.poixson.yumchain.YumChainDAO;
import com.poixson.yumchain.YumChainPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


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
		Component msg = Component.empty();
		msg = msg.append(Component.text("\nFoods:\n").color(NamedTextColor.GREEN));
		final Iterator<Entry<Material, Boolean>> it = chain.foods.entrySet().iterator();
		while (it.hasNext()) {
			final Entry<Material, Boolean> entry = it.next();
			final String name = entry.getKey().name();
			final boolean ate = entry.getValue().booleanValue();
			msg = msg
				.append(Component.text(String.format("  [%s] ", ate?"x":"_")).color(NamedTextColor.GREEN))
				.append(Component.text(name+"\n"                            ).color(NamedTextColor.WHITE));
		}
		player.sendMessage(msg);
		return true;
	}



	@Override
	public List<String> onTabComplete(final CommandSender sender, final String[] args) {
//TODO
System.out.println("TAB:"); for (final String arg : args) System.out.println("  "+arg);
return null;
	}



}
