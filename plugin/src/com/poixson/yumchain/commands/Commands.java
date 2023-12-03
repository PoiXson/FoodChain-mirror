package com.poixson.yumchain.commands;

import com.poixson.pluginlib.tools.commands.pxnCommandsHandler;
import com.poixson.yumchain.YumChainPlugin;


public class Commands extends pxnCommandsHandler<YumChainPlugin> {



	public Commands(final YumChainPlugin plugin) {
		super(plugin,
			"yum",
			"yumchain"
		);
		this.addCommand(new Command_List(plugin));
		this.addCommand(new Command_Reset(plugin));
	}



}
