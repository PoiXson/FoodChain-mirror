package com.poixson.yumchain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.poixson.commonmc.tools.plugin.xListener;


public class YumChainHandler extends xListener<YumChainPlugin> {

	protected final ConcurrentHashMap<UUID, YumChainDAO> chains = new ConcurrentHashMap<UUID, YumChainDAO>();



	public YumChainHandler(final YumChainPlugin plugin) {
		super(plugin);
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
			final YumChainDAO chain = new YumChainDAO(this.plugin, uuid);
			final YumChainDAO existing = this.chains.putIfAbsent(uuid, chain);
			return (existing==null ? chain : existing);
		}
	}



	public HashMap<UUID, Double> getChainPercents() {
		final HashMap<UUID, Double> percents = new HashMap<UUID, Double>();
		final Iterator<YumChainDAO> it = this.chains.values().iterator();
		while (it.hasNext()) {
			final YumChainDAO dao = it.next();
			percents.put(dao.uuid, Double.valueOf(dao.getChainPercent()));
		}
		return percents;
	}



}
