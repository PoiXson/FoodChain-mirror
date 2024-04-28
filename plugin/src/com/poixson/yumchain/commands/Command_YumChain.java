package com.poixson.yumchain.commands;

import com.poixson.tools.commands.pxnCommandRoot;
import com.poixson.yumchain.YumChainPlugin;


// /yumchain
public class Command_YumChain extends pxnCommandRoot {

	protected final Command_List  cmd_list;
	protected final Command_Reset cmd_reset;



	public Command_YumChain(final YumChainPlugin plugin) {
		super(
			plugin,
			"I don't feel like eating this", // desc,
			null, // usage
			null, // perm
			"yum", "yumchain", "yum-chain"
		);
		this.cmd_list  = new Command_List(plugin);
		this.cmd_reset = new Command_Reset(plugin);
	}



}
