package com.poixson.foodchain.listeners;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import com.poixson.foodchain.FoodChainPlugin;


public class FoodChainCommands implements CommandExecutor {
	public static final String CHAT_PREFIX = FoodChainPlugin.CHAT_PREFIX;

	protected final FoodChainPlugin plugin;

	protected final ArrayList<PluginCommand> cmds = new ArrayList<PluginCommand>();



	public FoodChainCommands(final FoodChainPlugin plugin) {
		this.plugin = plugin;
	}



	public void register() {
		final PluginCommand cmd = this.plugin.getCommand("roads");
		cmd.setExecutor(this);
		this.cmds.add(cmd);
		cmd.setTabCompleter( new FoodChainTabCompleter() );
	}
	public void unregister() {
		for (final PluginCommand cmd : this.cmds) {
			cmd.setExecutor(null);
		}
		this.cmds.clear();
	}



	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {
		final Player player = (sender instanceof Player ? (Player)sender : null);
		final int numargs = args.length;
		if (numargs >= 1) {
			switch (args[0]) {
			case "reset": {
//TODO
				return true;
			}
			case "set":
//TODO
				return true;
			default: break;
			}
		}
		return false;
	}



}
