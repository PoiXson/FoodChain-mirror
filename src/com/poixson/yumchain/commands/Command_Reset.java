package com.poixson.yumchain.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.poixson.commonmc.tools.commands.pxnCommand;
import com.poixson.yumchain.YumChainDAO;
import com.poixson.yumchain.YumChainPlugin;


public class CommandReset extends pxnCommand {

	protected final YumChainPlugin plugin;



	public CommandReset(final YumChainPlugin plugin) {
		super(
			"reset"
		);
		this.plugin = plugin;
	}



	@Override
	public boolean run(final CommandSender sender,
			final Command cmd, final String[] args) {
		final Player player = (sender instanceof Player ? (Player)sender : null);
//TODO: reset other player
		final YumChainDAO chain = this.plugin.getYumChain(player);
		chain.reset(true);
		return true;
	}



}
