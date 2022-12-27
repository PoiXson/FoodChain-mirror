package com.poixson.foodchain;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;


public class FoodChainHandler implements Listener {

	protected final FoodChainPlugin plugin;

	protected final ConcurrentHashMap<UUID, FoodChainDAO> chains = new ConcurrentHashMap<UUID, FoodChainDAO>();



	public FoodChainHandler(final FoodChainPlugin plugin) {
		this.plugin = plugin;
	}



	public void register() {
		Bukkit.getPluginManager()
			.registerEvents(this, this.plugin);
	}
	public void unregister() {
		HandlerList.unregisterAll(this);
	}



	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final FoodChainDAO chain = this.getFoodChain(player);
		if (chain == null) throw new NullPointerException("Unable to get food chain dao");
		chain.consume(event);
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		if (EntityType.PLAYER.equals(event.getEntityType())) {
			final Player player = (Player) event.getEntity();
			final FoodChainDAO chain = this.getFoodChain(player);
			if (chain == null) throw new NullPointerException("Unable to get food chain dao");
			chain.hunger(event, player);
		}
	}



	public FoodChainDAO getFoodChain(final Player player) {
		return this.getFoodChain(player.getUniqueId());
	}
	public FoodChainDAO getFoodChain(final UUID uuid) {
		// existing
		{
			final FoodChainDAO chain = this.chains.get(uuid);
			if (chain != null)
				return chain;
		}
		// new instance
		{
			final FoodChainDAO chain = new FoodChainDAO(uuid);
			final FoodChainDAO existing = this.chains.putIfAbsent(uuid, chain);
			return (existing==null ? chain : existing);
		}
	}



}
