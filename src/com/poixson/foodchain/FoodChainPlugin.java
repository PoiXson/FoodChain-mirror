package com.poixson.foodchain;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.poixson.foodchain.listeners.FoodChainCommands;


public class FoodChainPlugin extends JavaPlugin {
	public static final String LOG_PREFIX  = "[FoodChain] ";
	public static final String CHAT_PREFIX = ChatColor.AQUA + "[FoodChain] " + ChatColor.WHITE;
	public static final Logger log = Logger.getLogger("Minecraft");

	protected static final AtomicReference<FoodChainPlugin> instance = new AtomicReference<FoodChainPlugin>(null);

	// listeners
	protected final AtomicReference<FoodChainCommands> commandListener = new AtomicReference<FoodChainCommands>(null);
	protected final AtomicReference<FoodChainHandler> foodchains = new AtomicReference<FoodChainHandler>(null);



	public FoodChainPlugin() {
	}



	@Override
	public void onEnable() {
		if (!instance.compareAndSet(null, this))
			throw new RuntimeException("Plugin instance already enabled?");
		// commands listener
		{
			final FoodChainCommands listener = new FoodChainCommands(this);
			final FoodChainCommands previous = this.commandListener.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// food chain handler
		{
			final FoodChainHandler listener = new FoodChainHandler(this);
			final FoodChainHandler previous = this.foodchains.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
	}

	@Override
	public void onDisable() {
		// commands listener
		{
			final FoodChainCommands listener = this.commandListener.getAndSet(null);
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



	public FoodChainHandler getFoodChainHandler() {
		return this.foodchains.get();
	}



}
