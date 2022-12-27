package com.poixson.yumchain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.Bukkit;
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

	public final UUID uuid;

	public final HashMap<Material, Boolean> foods = new HashMap<Material, Boolean>();

	protected final AtomicLong handle_next_feed = new AtomicLong(0L);

	protected final AtomicInteger lastrnd = new AtomicInteger(0);



	public YumChainDAO(final UUID uuid) {
		this.uuid = uuid;
		this.reset();
	}



	public void reset() {
		this.foods.clear();
		this.foods.put(Material.APPLE,           Boolean.FALSE);
		this.foods.put(Material.MELON_SLICE,     Boolean.FALSE);
		this.foods.put(Material.SWEET_BERRIES,   Boolean.FALSE);
		this.foods.put(Material.GLOW_BERRIES,    Boolean.FALSE);
		this.foods.put(Material.CARROT,          Boolean.FALSE);
		this.foods.put(Material.BAKED_POTATO,    Boolean.FALSE);
		this.foods.put(Material.BEETROOT,        Boolean.FALSE);
		this.foods.put(Material.DRIED_KELP,      Boolean.FALSE);
		this.foods.put(Material.COOKED_BEEF,     Boolean.FALSE);
		this.foods.put(Material.COOKED_PORKCHOP, Boolean.FALSE);
		this.foods.put(Material.COOKED_MUTTON,   Boolean.FALSE);
		this.foods.put(Material.COOKED_CHICKEN,  Boolean.FALSE);
		this.foods.put(Material.COOKED_RABBIT,   Boolean.FALSE);
		this.foods.put(Material.COOKED_COD,      Boolean.FALSE);
		this.foods.put(Material.COOKED_SALMON,   Boolean.FALSE);
		this.foods.put(Material.BREAD,           Boolean.FALSE);
		this.foods.put(Material.COOKIE,          Boolean.FALSE);
		this.foods.put(Material.PUMPKIN_PIE,     Boolean.FALSE);
		this.foods.put(Material.MUSHROOM_STEW,   Boolean.FALSE);
		this.foods.put(Material.BEETROOT_SOUP,   Boolean.FALSE);
		this.foods.put(Material.RABBIT_STEW,     Boolean.FALSE);
		this.foods.put(Material.MILK_BUCKET,     Boolean.FALSE);
		this.foods.put(Material.HONEY_BOTTLE,    Boolean.FALSE);
		Bukkit.getPlayer(this.uuid)
			.sendMessage(this.getRandomYuck());
	}



	public void consume(final PlayerItemConsumeEvent event) {
		final ItemStack item = event.getItem();
		final Material type = item.getType();
		Boolean ate = this.foods.get(type);
		// food not in list
		if (ate == null) {
			switch (type) {
			case GOLDEN_APPLE:
			case ENCHANTED_GOLDEN_APPLE:
			case GLISTERING_MELON_SLICE:
			case GOLDEN_CARROT:
				this.handle_next_feed.set(0L);
				return;
			default: break;
			}
			this.handle_next_feed.set(Utils.GetMS());
			return;
		}
		// already ate
		if (ate.booleanValue()) {
			final Iterator<Entry<Material, Boolean>> it = this.foods.entrySet().iterator();
			while (it.hasNext()) {
				final Entry<Material, Boolean> entry = it.next();
				if (!type.equals(entry.getKey())) {
					if (entry.getValue().booleanValue()) {
						this.reset();
						this.foods.put(type, Boolean.TRUE);
						return;
					}
				}
			}
		}
		// first time eating
		this.foods.put(type, Boolean.TRUE);
		return;
	}

	public void hunger(final FoodLevelChangeEvent event, final Player player) {
		final long last = this.handle_next_feed.get();
		if (last == 0L) return;
		this.handle_next_feed.set(0L);
		if (Utils.GetMS() < last + HUNGER_SEQ_TIMEOUT) {
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
				player.sendMessage(String.format(
					"%s %d/%d",
					this.getRandomYum(),
					ate, total
				));
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
		return "Yum!";
	}
	public String getRandomYuck() {
		while (true) {
			final int rnd = NumberUtils.GetNewRandom(0, 5, this.lastrnd.get());
			this.lastrnd.set(rnd);
			switch (rnd) {
			case 1: return "Yuck";
			case 2: return "Blah";
			case 3: return "Eh..";
			}
		}
	}



}
