package com.poixson.yumchain.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.poixson.commonbukkit.tools.commands.pxnCommandsHandler;
import com.poixson.yumchain.YumChainPlugin;


public class YumChainCommands extends pxnCommandsHandler {



	public YumChainCommands(final YumChainPlugin plugin) {
		super(
			plugin,
			"yum",
			"yumchain"
		);
		this.addCommand(new CommandReset(plugin));
	}



	@Override
	public List<String> onTabComplete(
			final CommandSender sender, final Command cmd,
			final String label, final String[] args) {
		final List<String> matches = new ArrayList<String>();
		final int size = args.length;
		switch (size) {
		case 1:
			if ("reset".startsWith(args[0])) matches.add("reset");
			break;
		default:
		}
		return matches;
	}



}
