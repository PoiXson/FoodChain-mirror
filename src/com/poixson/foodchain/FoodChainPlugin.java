package com.poixson.foodchain;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

	protected final ConcurrentHashMap<UUID, FoodChain> chains = new ConcurrentHashMap<UUID, FoodChain>();



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



	public FoodChain getFoodChain(final Player player) {
		return this.getFoodChain(player.getUniqueId());
	}
	public FoodChain getFoodChain(final UUID uuid) {
		// existing
		{
			final FoodChain chain = this.chains.get(uuid);
			if (chain != null)
				return chain;
		}
		// new instance
		{
			final FoodChain chain = new FoodChain(uuid);
			final FoodChain existing = this.chains.putIfAbsent(uuid, chain);
			if (existing != null) {
				chain.unregister();
				return existing;
			}
			return chain;
		}
	}



}
