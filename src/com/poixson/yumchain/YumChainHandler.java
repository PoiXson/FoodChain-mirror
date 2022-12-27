package com.poixson.yumchain;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;


public class YumChainHandler implements Listener {

	protected final YumChainPlugin plugin;

	protected final ConcurrentHashMap<UUID, YumChainDAO> chains = new ConcurrentHashMap<UUID, YumChainDAO>();



	public YumChainHandler(final YumChainPlugin plugin) {
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
		final YumChainDAO chain = this.getYumChain(player);
		if (chain == null) throw new NullPointerException("Unable to get yum chain dao");
		chain.consume(event);
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onFoodLevelChange(final FoodLevelChangeEvent event) {
		if (EntityType.PLAYER.equals(event.getEntityType())) {
			final Player player = (Player) event.getEntity();
			final YumChainDAO chain = this.getYumChain(player);
			if (chain == null) throw new NullPointerException("Unable to get yum chain dao");
			chain.hunger(event, player);
		}
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		if (!event.getKeepLevel()) {
			this.getYumChain(event.getEntity())
				.reset(false);
		}
	}



	public YumChainDAO getYumChain(final Player player) {
		return this.getYumChain(player.getUniqueId());
	}
	public YumChainDAO getYumChain(final UUID uuid) {
		// existing
		{
			final YumChainDAO chain = this.chains.get(uuid);
			if (chain != null)
				return chain;
		}
		// new instance
		{
			final YumChainDAO chain = new YumChainDAO(uuid);
			final YumChainDAO existing = this.chains.putIfAbsent(uuid, chain);
			return (existing==null ? chain : existing);
		}
	}



}
