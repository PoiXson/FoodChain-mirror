package com.poixson.yumchain.commands;

import java.io.Closeable;

import com.poixson.yumchain.YumChainPlugin;


public class Commands implements Closeable {

	// /yumchain
	protected final Command_YumChain cmd_yumchain;



	public Commands(final YumChainPlugin plugin) {
		this.cmd_yumchain = new Command_YumChain(plugin);
	}



	@Override
	public void close() {
		this.cmd_yumchain.close();
	}



}
