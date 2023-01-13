package com.poixson.yumchain.commands;

import com.poixson.commonmc.tools.commands.pxnCommandsHandler;
import com.poixson.yumchain.YumChainPlugin;


public class Commands extends pxnCommandsHandler {



	public Commands(final YumChainPlugin plugin) {
		super(
			plugin,
			"yum",
			"yumchain"
		);
		this.addCommand(new CommandList(plugin));
		this.addCommand(new CommandReset(plugin));
	}



}
