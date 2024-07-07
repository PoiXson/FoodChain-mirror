package com.poixson.yumchain;

import static com.poixson.utils.BukkitUtils.Log;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;


public class YumChainAPI {

	protected static final String NAME  = "YumChain";
	protected static final String CLASS = "com.poixson.yumchain.YumChainPlugin";

	protected final YumChainPlugin plugin;

	protected static final AtomicInteger errcount_PluginNotFound = new AtomicInteger(0);



	public static YumChainAPI GetAPI() {
		// existing instance
		{
			final ServicesManager services = Bukkit.getServicesManager();
			final YumChainAPI api = services.load(YumChainAPI.class);
			if (api != null)
				return api;
		}
		// load api
		try {
			if (Class.forName(CLASS) == null)
				throw new ClassNotFoundException(CLASS);
			final PluginManager manager = Bukkit.getPluginManager();
			final Plugin plugin = manager.getPlugin(NAME);
			if (plugin == null) throw new RuntimeException(NAME+" plugin not found");
			return new YumChainAPI(plugin);
		} catch (ClassNotFoundException e) {
			if (errcount_PluginNotFound.getAndIncrement() < 10)
				Log().severe("Plugin not found: "+NAME);
			return null;
		}
	}

	protected YumChainAPI(final Plugin p) {
		if (p == null) throw new NullPointerException();
		this.plugin = (YumChainPlugin) p;
	}



}
