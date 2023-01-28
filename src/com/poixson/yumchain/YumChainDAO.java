package com.poixson.yumchain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import com.poixson.utils.NumberUtils;


public class YumChainDAO {
	public static final double HUNGER_MULTIPLIER  = 4.0;

	protected final YumChainPlugin plugin;

	public final UUID uuid;

	public final HashMap<Material, Boolean> foods = new HashMap<Material, Boolean>();

	protected final AtomicInteger lastrnd_yum  = new AtomicInteger(0);
	protected final AtomicInteger lastrnd_yuck = new AtomicInteger(0);

	protected final AtomicBoolean quietyum = new AtomicBoolean(false);
	protected final AtomicBoolean bypass   = new AtomicBoolean(false);



	public YumChainDAO(final YumChainPlugin plugin, final UUID uuid) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.reset(false);
	}



	public void reset() {
		this.reset(true);
	}
	public void reset(final boolean sendmsg) {
		this.foods.clear();
		final Material[] foods = this.plugin.getChainFoodsMat();
		for (final Material food : foods) {
			this.foods.put(food, Boolean.FALSE);
		}
		if (sendmsg) {
			Bukkit.getPlayer(this.uuid)
				.sendMessage(ChatColor.AQUA+this.getRandomYuck());
		}
	}



	public void consume(final PlayerItemConsumeEvent event) {
		final ItemStack item = event.getItem();
		final Material type = item.getType();
		Boolean ate = this.foods.get(type);
		// bypass
		if (ate == null) {
			this.quietyum.set(true);
			if (this.plugin.isBypassFood(type))
				this.bypass.set(true);
			return;
		}
		// already ate
		if (ate.booleanValue()) {
			this.reset(true);
			this.quietyum.set(true);
		}
		this.foods.put(type, Boolean.TRUE);
	}

	public void hunger(final FoodLevelChangeEvent event, final Player player) {
		if (this.bypass.getAndSet(false))
			return;
		int lvl = player.getFoodLevel();
		final int delta = event.getFoodLevel() - lvl;
		final double percent = this.getChainPercent();
		// hunger
		if (delta < 0) {
			final double hunger = ((double)delta) * (1.0 - percent) * HUNGER_MULTIPLIER;
			event.setFoodLevel(lvl + (int)Math.ceil(hunger));
		// feed
		} else {
			final int ate   = this.getChainAte();
			final int total = this.getFoodsCount();
			event.setFoodLevel(lvl + ate);
			if (!this.quietyum.getAndSet(false)) {
				player.sendMessage(String.format(
					"%s [%d/%d] %s",
					ChatColor.AQUA,
					Integer.valueOf(ate),
					Integer.valueOf(total),
					this.getRandomYum()
				));
				if (percent >= 1.0) {
					player.sendMessage(ChatColor.AQUA+"Yum chain is full, no more hunger!");
					YumChainPlugin.log.info(YumChainPlugin.LOG_PREFIX+"Yum chain is full: "+player.getName());
				}
			}
		}
	}



	public double getChainPercent() {
		return ((double)this.getChainAte()) / ((double)this.getFoodsCount());
	}
	public int getChainAte() {
		int total = 0;
		final Iterator<Boolean> it = this.foods.values().iterator();
		while (it.hasNext()) {
			final Boolean ate = it.next();
			if (ate.booleanValue())
				total++;
		}
		return total;
	}
	public int getFoodsCount() {
		return this.foods.size();
	}



	public String getRandomYum() {
		final String[] msgs = this.plugin.getYumMessages();
		final int rnd = NumberUtils.GetNewRandom(0, msgs.length-1, this.lastrnd_yum.get());
		this.lastrnd_yum.set(rnd);
		return msgs[rnd];
	}
	public String getRandomYuck() {
		final String[] msgs = this.plugin.getYuckMessages();
		final int rnd = NumberUtils.GetNewRandom(0, msgs.length-1, this.lastrnd_yuck.get());
		this.lastrnd_yuck.set(rnd);
		return msgs[rnd];
	}



}
