package com.poixson.yumchain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import com.poixson.tools.xRand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


public class YumChainDAO {
	public static final double HUNGER_MULTIPLIER  = 4.0;

	protected final YumChainPlugin plugin;

	public final UUID uuid;

	public final HashMap<Material, Boolean> foods = new HashMap<Material, Boolean>();

	protected final AtomicBoolean quietyum = new AtomicBoolean(true);
	protected final AtomicBoolean bypass   = new AtomicBoolean(true);

	protected final xRand random = (new xRand()).seed_time();



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
			final Player player = Bukkit.getPlayer(this.uuid);
			player.sendMessage(Component.text(this.getRandomYuck()).color(NamedTextColor.AQUA));
		}
	}



	// note: not triggered by cake
	public void consume(final PlayerItemConsumeEvent event) {
		final ItemStack item = event.getItem();
		final Material type = item.getType();
		Boolean ate = this.foods.get(type);
		// bypass
		if (ate == null) {
			this.quietyum.set(true);
			this.bypass.set( this.plugin.isBypassFood(type) );
			return;
		}
		this.bypass.set(false);
		// already ate
		if (ate.booleanValue()) {
			this.reset(true);
			this.quietyum.set(true);
		} else {
			this.quietyum.set(false);
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
			if (!this.quietyum.getAndSet(true)) {
				player.sendMessage(Component.text(String.format(
					" [%d/%d] %s",
					Integer.valueOf(ate),
					Integer.valueOf(total),
					this.getRandomYum()
				)).color(NamedTextColor.AQUA));
				if (percent >= 1.0) {
					player.sendMessage(Component.text("Yum chain is full, no more hunger!").color(NamedTextColor.AQUA));
					this.log().info("Yum chain is full: "+player.getName());
					// fill hunger bar
					if (player.getFoodLevel()< 20) {
						final FoodFullerTask task = new FoodFullerTask(this.plugin, player);
						task.start();
					}
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
		final int rnd = this.random.nextInt(0, msgs.length-1);
		return msgs[rnd];
	}
	public String getRandomYuck() {
		final String[] msgs = this.plugin.getYuckMessages();
		final int rnd = this.random.nextInt(0, msgs.length-1);
		return msgs[rnd];
	}



	public Logger log() {
		return this.plugin.log();
	}



}
