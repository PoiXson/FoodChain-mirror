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
import com.poixson.utils.Utils;


public class YumChainDAO {
	public static final double HUNGER_MULTIPLIER  = 4.0;
	public static final long   HUNGER_SEQ_TIMEOUT = 5000L;

	protected final YumChainPlugin plugin;

	public final UUID uuid;

	public final HashMap<Material, Boolean> foods = new HashMap<Material, Boolean>();

	protected final AtomicInteger lastrnd = new AtomicInteger(0);
	protected final AtomicBoolean quietyum = new AtomicBoolean(false);



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
		// food not in list
		if (ate == null) {
			this.quietyum.set(true);
			switch (type) {
			case GOLDEN_APPLE:
			case ENCHANTED_GOLDEN_APPLE:
			case GLISTERING_MELON_SLICE:
			case GOLDEN_CARROT:
				return;
			default: break;
			}
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
			int lvl = player.getFoodLevel();
			final int delta = event.getFoodLevel() - lvl;
			// hunger
			if (delta < 0) {
				final double percent = this.getChainPercent();
				final double hunger = ((double)delta) * (1.0 - percent) * HUNGER_MULTIPLIER;
				event.setFoodLevel(lvl + (int)Math.ceil(hunger));
				// feed
			} else {
				final int ate   = this.getChainAte();
				final int total = this.getFoodsCount();
				event.setFoodLevel(lvl + ate);
				if (this.quietyum.get()) {
					this.quietyum.set(false);
				} else {
					player.sendMessage(String.format(
						"%s [%d/%d] %s",
						ChatColor.AQUA,
						ate, total,
						this.getRandomYum()
					));
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
		for (int i=0; i<3; i++) {
			final int rnd = NumberUtils.GetNewRandom(0, 10, this.lastrnd.get());
			this.lastrnd.set(rnd);
			switch (rnd) {
			case 1: return "Yum!";
			case 2: return "Burp";
			case 3: return "Mmmm";
			default: break;
			}
		}
		return "Yum!";
	}
	public String getRandomYuck() {
		for (int i=0; i<3; i++) {
			final int rnd = NumberUtils.GetNewRandom(0, 10, this.lastrnd.get());
			this.lastrnd.set(rnd);
			switch (rnd) {
			case 1: return "Yuck";
			case 2: return "Blah";
			case 3: return "Ugh";
			case 4: return "Eh..";
			case 5: return "Ew..";
			default: break;
			}
		}
		return "Yuck";
	}



}
