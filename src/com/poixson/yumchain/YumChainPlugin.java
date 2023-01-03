package com.poixson.yumchain;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.poixson.yumchain.commands.YumChainCommands;


public class YumChainPlugin extends JavaPlugin {
	public static final String LOG_PREFIX  = "[YUM] ";
	public static final String CHAT_PREFIX = ChatColor.AQUA + "[YUM] " + ChatColor.WHITE;
	public static final Logger log = Logger.getLogger("Minecraft");

	protected static final AtomicReference<YumChainPlugin> instance = new AtomicReference<YumChainPlugin>(null);
	protected static final AtomicReference<Metrics>        metrics  = new AtomicReference<Metrics>(null);

	// listeners
	protected final AtomicReference<YumChainCommands> commandListener = new AtomicReference<YumChainCommands>(null);
	protected final AtomicReference<YumChainHandler> yumchains = new AtomicReference<YumChainHandler>(null);



	public YumChainPlugin() {
	}



	@Override
	public void onEnable() {
		if (!instance.compareAndSet(null, this))
			throw new RuntimeException("Plugin instance already enabled?");
		// commands listener
		{
			final YumChainCommands listener = new YumChainCommands(this);
			final YumChainCommands previous = this.commandListener.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// yum chain handler
		{
			final YumChainHandler listener = new YumChainHandler(this);
			final YumChainHandler previous = this.yumchains.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// bStats
		System.setProperty("bstats.relocatecheck","false");
		metrics.set(new Metrics(this, 17233));
	}

	@Override
	public void onDisable() {
		// commands listener
		{
			final YumChainCommands listener = this.commandListener.getAndSet(null);
			if (listener != null)
				listener.unregister();
		}
		// stop schedulers
		try {
			Bukkit.getScheduler()
				.cancelTasks(this);
		} catch (Exception ignore) {}
		// stop listeners
		HandlerList.unregisterAll(this);
		if (!instance.compareAndSet(this, null))
			throw new RuntimeException("Disable wrong instance of plugin?");
	}



	public YumChainHandler getYumChainHandler() {
		return this.yumchains.get();
	}
	public YumChainDAO getYumChain(final Player player) {
		return this.getYumChainHandler().getYumChain(player);
	}
	public YumChainDAO getYumChain(final UUID uuid) {
		return this.getYumChainHandler().getYumChain(uuid);
	}



}
